package io.github.blueprintplatform.codegen.adapter.out.version;

import static io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion.V4_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootVersionResolutionException;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Source;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLHandshakeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.context.support.ResourceBundleMessageSource;

class SpringBootTargetResolverTest {
  private HttpServer server;
  private HttpClient client;
  private URI endpoint;

  @BeforeEach
  void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    endpoint = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
    server.start();
    client = HttpClient.newHttpClient();
  }

  @AfterEach
  void stop() {
    server.stop(0);
    client.close();
  }

  @Test
  void picksNumericStableMaximumAndChecksInheritedParentCoordinates() {
    metadata(
        "<version>4.1.9</version><version>4.1.10</version>"
            + "<version>4.2.99</version><version>4.1.11-RC1</version><version>4.1.12-SNAPSHOT</version>");
    respond(
        "/4.1.10/spring-boot-starter-parent-4.1.10.pom",
        200,
        "<project><parent><groupId>org.springframework.boot</groupId><version>4.1.10</version>"
            + "</parent><artifactId>spring-boot-starter-parent</artifactId></project>");
    assertThat(resolver().resolve(request(V4_1)))
        .isEqualTo(
            new TargetResolution(
                new SpringBootJvmTarget(JavaVersion.JAVA_21, V4_1, "4.1.10"),
                Source.REMOTE_LATEST,
                null));
  }

  @Test
  void missingParentDoesNotFallback() {
    metadata("<version>4.1.3</version>");
    var resolver = resolver();
    assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
        .isInstanceOfSatisfying(
            SpringBootVersionResolutionException.class,
            failure ->
                assertThat(localized(failure))
                    .endsWith("Central parent POM failed: Central parent POM returned HTTP 404"));
  }

  @Test
  void wrongParentCoordinatesDoNotFallback() {
    metadata("<version>4.1.3</version>");
    respond(
        "/4.1.3/spring-boot-starter-parent-4.1.3.pom",
        200,
        "<project><groupId>org.springframework.boot</groupId><version>4.1.4</version>"
            + "<artifactId>spring-boot-starter-parent</artifactId></project>");

    assertThatThrownBy(() -> resolver().resolve(request(V4_1)))
        .isInstanceOfSatisfying(
            SpringBootVersionResolutionException.class,
            failure ->
                assertThat(localized(failure))
                    .endsWith("Unexpected Central parent POM coordinates"));
  }

  @Test
  void malformedMetadataDoesNotFallback() {
    respond("/maven-metadata.xml", 200, "<metadata>");
    var resolver = resolver();
    assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
        .isInstanceOf(SpringBootVersionResolutionException.class)
        .hasMessage("adapter.springboot.resolution.failed");
  }

  @Test
  void oversizedResponseAfterHeadersDoesNotFallback() {
    respond("/maven-metadata.xml", 200, " ".repeat(1024 * 1024 + 1));
    var resolver = resolver();

    assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
        .isInstanceOfSatisfying(
            SpringBootVersionResolutionException.class,
            failure -> assertThat(localized(failure)).endsWith("Central response exceeded byte limit"));
  }

  @Test
  @Timeout(8)
  void stalledBodyUsesRemainingBudgetWithoutFallback() {
    var releaseBody = new CountDownLatch(1);
    server.createContext(
        "/stall",
        exchange -> {
          try (exchange) {
            exchange.sendResponseHeaders(200, 32);
            exchange.getResponseBody().write(1);
            exchange.getResponseBody().flush();
            try {
              releaseBody.await();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        });
    var repository = new MavenCentralHttpClient(client, endpoint);
    long deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();

    try {
      assertThatThrownBy(() -> repository.read("metadata", "stall", 32, deadline))
          .isInstanceOf(SpringBootVersionResolutionException.class);
    } finally {
      releaseBody.countDown();
    }
  }

  @Test
  void unavailableConnectionSelectsBundledPatchForEachLine() {
    server.stop(0);
    var resolver = resolver();
    for (var entry : bundled().entrySet()) {
      var selected = resolver.resolve(request(entry.getKey()));
      assertThat(selected.target())
          .isEqualTo(new SpringBootJvmTarget(JavaVersion.JAVA_21, entry.getKey(), entry.getValue()));
      assertThat(selected.source()).isEqualTo(Source.BUNDLED_FALLBACK);
      assertThat(selected.warning().messageKey()).isEqualTo("adapter.springboot.repository.unavailable");
      assertThat(selected.warning().arguments()).containsExactly("metadata", "ConnectException");
    }
  }

  @Test
  void tlsFailureDoesNotFallbackEvenWithNestedConnectionError() {
    var tlsFailure = new SSLHandshakeException("Certificate rejected");
    tlsFailure.initCause(new ConnectException("Connection failed"));
    var tlsClient = mock(HttpClient.class);
    when(tlsClient.sendAsync(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(tlsFailure));
    var resolver = new SpringBootTargetResolver(tlsClient, endpoint, bundled());

    assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
        .isInstanceOf(SpringBootVersionResolutionException.class)
        .hasCause(tlsFailure);
  }

  @Test
  void connectionWrapperDoesNotHideTlsFailure() {
    var connectionFailure = new ConnectException("Connection failed");
    connectionFailure.initCause(new SSLHandshakeException("Certificate rejected"));
    var tlsClient = mock(HttpClient.class);
    when(tlsClient.sendAsync(any(), any()))
        .thenReturn(CompletableFuture.failedFuture(connectionFailure));
    var resolver = new SpringBootTargetResolver(tlsClient, endpoint, bundled());

    assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
        .isInstanceOf(SpringBootVersionResolutionException.class)
        .hasCause(connectionFailure);
  }

  @Test
  @Timeout(8)
  void interruptionBeforeHeadersPreservesTheFlagAndDoesNotFallback() throws Exception {
    var requestArrived = new CountDownLatch(1);
    var releaseHeaders = new CountDownLatch(1);
    server.createContext(
        "/maven-metadata.xml",
        exchange -> {
          try (exchange) {
            requestArrived.countDown();
            releaseHeaders.await();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });
    var resolver = resolver();
    var result =
        new FutureTask<>(
            () -> {
              assertThatThrownBy(() -> resolver.resolve(request(V4_1)))
                  .isInstanceOfSatisfying(
                      SpringBootVersionResolutionException.class,
                      failure ->
                          assertThat(localized(failure)).endsWith("Central lookup interrupted"))
                  .hasRootCauseInstanceOf(InterruptedException.class);
              return Thread.currentThread().isInterrupted();
            });
    var worker = Thread.startVirtualThread(result);

    try {
      assertThat(requestArrived.await(3, TimeUnit.SECONDS)).isTrue();
      worker.interrupt();
      assertThat(result.get(3, TimeUnit.SECONDS)).isTrue();
    } finally {
      releaseHeaders.countDown();
    }
  }

  private static String localized(SpringBootVersionResolutionException failure) {
    var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    return messages.getMessage(failure.getMessageKey(), failure.getArgs(), Locale.ENGLISH);
  }

  private SpringBootTargetResolver resolver() {
    return new SpringBootTargetResolver(client, endpoint, bundled());
  }

  private static SpringBootJvmTarget request(SpringBootVersion line) {
    return new SpringBootJvmTarget(JavaVersion.JAVA_21, line);
  }

  private static Map<SpringBootVersion, String> bundled() {
    return Map.of(V4_1, "4.1.3", SpringBootVersion.V4_2, "4.2.0");
  }

  private void metadata(String entries) {
    respond(
        "/maven-metadata.xml",
        200,
        "<metadata><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-parent"
            + "</artifactId><versioning><versions>"
            + entries
            + "</versions></versioning></metadata>");
  }

  private void respond(String path, int status, String body) {
    server.createContext(
        path,
        exchange -> {
          try (exchange) {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
          }
        });
  }
}
