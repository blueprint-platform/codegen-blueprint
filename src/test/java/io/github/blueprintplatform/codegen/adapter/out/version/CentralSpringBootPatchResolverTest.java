package io.github.blueprintplatform.codegen.adapter.out.version;

import static io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion.V4_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootRepositoryUnavailableException;
import io.github.blueprintplatform.codegen.application.usecase.project.BootVersionSelection;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CentralSpringBootPatchResolverTest {
  private HttpServer server;
  private URI endpoint;

  @BeforeEach
  void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    endpoint = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
    server.start();
  }

  @AfterEach
  void stop() {
    server.stop(0);
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
    assertThat(selection().select(V4_1))
        .isEqualTo(new BootVersionSelection.Result("4.1.10", "CENTRAL_LATEST", null));
  }

  @Test
  void missingParentDoesNotFallback() {
    metadata("<version>4.1.3</version>");
    var versions = selection();
    assertThatThrownBy(() -> versions.select(V4_1))
        .isInstanceOf(RuntimeException.class)
        .isNotInstanceOf(SpringBootRepositoryUnavailableException.class)
        .hasRootCauseMessage("Central parent POM returned HTTP 404");
  }

  @Test
  void malformedMetadataDoesNotFallback() {
    respond("/maven-metadata.xml", 200, "<metadata>");
    var versions = selection();
    assertThatThrownBy(() -> versions.select(V4_1))
        .isInstanceOf(RuntimeException.class)
        .isNotInstanceOf(SpringBootRepositoryUnavailableException.class)
        .hasMessage("adapter.springboot.resolution.failed");
  }

  @Test
  void unavailableConnectionSelectsBundledPatchForEachLine() {
    server.stop(0);
    var versions = selection();
    for (var entry : bundled().entrySet()) {
      var selected = versions.select(entry.getKey());
      assertThat(selected.patch()).isEqualTo(entry.getValue());
      assertThat(selected.source()).isEqualTo("BUNDLED_FALLBACK");
      assertThat(selected.warning()).contains("metadata");
    }
  }

  private BootVersionSelection selection() {
    return new BootVersionSelection(new CentralSpringBootPatchResolver(endpoint), bundled());
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
