package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootResolutionException;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootRepositoryUnavailableException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.nio.channels.UnresolvedAddressException;
import java.time.Duration;
import javax.net.ssl.SSLException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;

/** One bounded Central request, including the response body. */
public final class CentralRepositoryHttp {
  private final URI base;
  private final HttpClient client;

  public CentralRepositoryHttp(HttpClient client, URI base) {
    this.client = client;
    this.base = base;
  }

  byte[] read(String phase, String path, int limit, long deadline) {
    long remaining = Math.min(Duration.ofSeconds(5).toNanos(), deadline - System.nanoTime());
    if (remaining <= 0)
      throw new SpringBootResolutionException("Central resolution deadline exceeded");
    // The factory timeout covers headers and body. Configure it per request for the shared budget.
    var factory = new JdkClientHttpRequestFactory(client);
    factory.setReadTimeout(Duration.ofNanos(remaining));
    ClientHttpResponse response;
    try {
      response = factory.createRequest(base.resolve(path), HttpMethod.GET).execute();
    } catch (IOException e) {
      if (Thread.currentThread().isInterrupted()) {
        throw new SpringBootResolutionException("Central lookup interrupted", e);
      }
      if (isConnectionFailure(e)) {
        throw new SpringBootRepositoryUnavailableException(phase, e);
      }
      throw failed(phase, e);
    }

    // Close the stream first so Spring's response close does not drain untrusted excess bytes.
    try (response;
        var body = response.getBody()) {
      int status = response.getStatusCode().value();
      if (status != 200) {
        throw new IOException("Central " + phase + " returned HTTP " + status);
      }
      byte[] bytes = body.readNBytes(limit + 1);
      if (bytes.length > limit) {
        throw new IOException("Central response exceeded byte limit");
      }
      return bytes;
    } catch (IOException e) {
      throw failed(phase, e);
    }
  }

  private static SpringBootResolutionException failed(String phase, Throwable cause) {
    return new SpringBootResolutionException(
        "Central " + phase + " failed: " + cause.getMessage(), cause);
  }

  private static boolean isConnectionFailure(Throwable cause) {
    if (cause instanceof SSLException) return false;
    if (cause instanceof HttpTimeoutException
        || cause instanceof ConnectException
        || cause instanceof NoRouteToHostException
        || cause instanceof UnknownHostException
        || cause instanceof UnresolvedAddressException) {
      return true;
    }
    return cause.getCause() != null && isConnectionFailure(cause.getCause());
  }
}
