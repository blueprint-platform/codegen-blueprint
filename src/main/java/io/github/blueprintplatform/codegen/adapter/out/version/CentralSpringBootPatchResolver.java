package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootResolutionException;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootPatchResolver;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootRepositoryUnavailableException;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class CentralSpringBootPatchResolver implements SpringBootPatchResolver {
  private static final String GROUP = "org.springframework.boot";
  private static final String ARTIFACT = "spring-boot-starter-parent";
  private final URI base;
  private final HttpClient client;

  public CentralSpringBootPatchResolver() {
    this(
        URI.create(
            "https://repo.maven.apache.org/maven2/org/springframework/boot/" + ARTIFACT + "/"));
  }

  CentralSpringBootPatchResolver(URI base) {
    this.base = base;
    this.client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
  }

  @Override
  public String resolveLatest(SpringBootVersion line) {
    try {
      return resolve(line);
    } catch (IllegalStateException e) {
      throw new SpringBootResolutionException(e.getMessage(), e);
    }
  }

  private String resolve(SpringBootVersion line) {
    long deadline = System.nanoTime() + Duration.ofSeconds(8).toNanos();
    Element metadata = read("metadata", "maven-metadata.xml", 1024 * 1024, deadline);
    require(
        "metadata".equals(metadata.getTagName())
            && GROUP.equals(value(metadata, "groupId"))
            && ARTIFACT.equals(value(metadata, "artifactId")),
        "Unexpected Central metadata coordinates");
    Element versions = child(child(metadata, "versioning"), "versions");
    var candidates = new ArrayList<String>();
    for (Node node = versions == null ? null : versions.getFirstChild();
        node != null;
        node = node.getNextSibling()) {
      if (node instanceof Element version && "version".equals(version.getTagName())) {
        String patch = version.getTextContent().strip();
        if (line.matchesPatch(patch)) candidates.add(patch);
      }
    }
    String patch =
        candidates.stream()
            .max(
                Comparator.comparingInt(
                    candidate -> Integer.parseInt(candidate.substring(line.key().length() + 1))))
            .orElseThrow(
                () -> new IllegalStateException("No stable Central Spring Boot patch for " + line));
    Element pom =
        read("parent POM", patch + "/" + ARTIFACT + "-" + patch + ".pom", 512 * 1024, deadline);
    Element parent = child(pom, "parent");
    require(
        "project".equals(pom.getTagName())
            && ARTIFACT.equals(value(pom, "artifactId"))
            && GROUP.equals(inherited(pom, parent, "groupId"))
            && patch.equals(inherited(pom, parent, "version")),
        "Unexpected Central parent POM coordinates");
    return patch;
  }

  private Element read(String phase, String path, int limit, long deadline) {
    long remaining = Math.min(Duration.ofSeconds(5).toNanos(), deadline - System.nanoTime());
    if (remaining <= 0) throw new IllegalStateException("Central resolution deadline exceeded");
    var headersReceived = new AtomicBoolean();
    var request =
        HttpRequest.newBuilder(base.resolve(path)).timeout(Duration.ofNanos(remaining)).build();
    var response =
        client.sendAsync(
            request,
            info -> {
              headersReceived.set(true);
              return new BoundedBody(limit);
            });
    try {
      var result = response.get(remaining, TimeUnit.NANOSECONDS);
      require(
          result.statusCode() == 200, "Central " + phase + " returned HTTP " + result.statusCode());
      var factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      var parser = factory.newDocumentBuilder();
      parser.setErrorHandler(
          new DefaultHandler() {
            @Override
            public void fatalError(SAXParseException e) throws SAXException {
              throw e;
            }
          });
      return parser.parse(new ByteArrayInputStream(result.body())).getDocumentElement();
    } catch (InterruptedException e) {
      response.cancel(true);
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Central lookup interrupted", e);
    } catch (Exception e) {
      response.cancel(true);
      Throwable cause = e instanceof ExecutionException ? e.getCause() : e;
      if (!headersReceived.get() && isConnectionFailure(cause)) {
        throw new SpringBootRepositoryUnavailableException(phase, cause);
      }
      throw new IllegalStateException("Central " + phase + " failed: " + cause.getMessage(), cause);
    }
  }

  private static boolean isConnectionFailure(Throwable cause) {
    if (cause instanceof javax.net.ssl.SSLException) return false;
    if (cause instanceof HttpTimeoutException
        || cause instanceof TimeoutException
        || cause instanceof ConnectException
        || cause instanceof NoRouteToHostException
        || cause instanceof UnknownHostException
        || cause instanceof UnresolvedAddressException) {
      return true;
    }
    return cause.getCause() != null && isConnectionFailure(cause.getCause());
  }

  private static Element child(Element parent, String name) {
    if (parent == null) return null;
    for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element element && name.equals(element.getTagName())) return element;
    }
    return null;
  }

  private static String value(Element parent, String name) {
    Element element = child(parent, name);
    return element == null ? null : element.getTextContent().strip();
  }

  private static String inherited(Element pom, Element parent, String name) {
    String direct = value(pom, name);
    return direct == null ? value(parent, name) : direct;
  }

  private static void require(boolean valid, String message) {
    if (!valid) throw new IllegalStateException(message);
  }

  private static final class BoundedBody implements HttpResponse.BodySubscriber<byte[]> {
    private final int limit;
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final CompletableFuture<byte[]> result = new CompletableFuture<>();
    private Flow.Subscription subscription;

    private BoundedBody(int limit) {
      this.limit = limit;
    }

    @Override
    public CompletableFuture<byte[]> getBody() {
      return result;
    }

    @Override
    public void onSubscribe(Flow.Subscription incoming) {
      subscription = incoming;
      subscription.request(1);
    }

    @Override
    public void onNext(List<ByteBuffer> buffers) {
      for (ByteBuffer buffer : buffers) {
        if (buffer.remaining() > limit - bytes.size()) {
          subscription.cancel();
          result.completeExceptionally(new IOException("Central response exceeded byte limit"));
          return;
        }
        byte[] chunk = new byte[buffer.remaining()];
        buffer.get(chunk);
        bytes.writeBytes(chunk);
      }
      subscription.request(1);
    }

    @Override
    public void onError(Throwable error) {
      result.completeExceptionally(error);
    }

    @Override
    public void onComplete() {
      result.complete(bytes.toByteArray());
    }
  }
}
