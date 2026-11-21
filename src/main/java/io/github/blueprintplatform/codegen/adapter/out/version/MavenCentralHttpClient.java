package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.MavenCentralUnavailableException;
import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootVersionResolutionException;
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
final class MavenCentralHttpClient {
  private final URI base;
  private final HttpClient client;

  MavenCentralHttpClient(HttpClient client, URI base) {
    this.client = client;
    this.base = base;
  }

  byte[] read(String phase, String path, int limit, long deadline) {
    long remaining = Math.min(Duration.ofSeconds(5).toNanos(), deadline - System.nanoTime());
    if (remaining <= 0) {
      throw new SpringBootVersionResolutionException("adapter.springboot.resolution.deadline");
    }
    // The factory timeout covers headers and body. Configure it per request for the shared budget.
    var factory = new JdkClientHttpRequestFactory(client);
    factory.setReadTimeout(Duration.ofNanos(remaining));
    ClientHttpResponse response;
    try {
      response = factory.createRequest(base.resolve(path), HttpMethod.GET).execute();
    } catch (IOException e) {
      if (Thread.currentThread().isInterrupted()) {
        throw new SpringBootVersionResolutionException("adapter.springboot.resolution.interrupted", e);
      }
      if (isConnectionFailure(e)) {
        throw new MavenCentralUnavailableException(phase, e);
      }
      throw failed(phase, e);
    }

    // Close the stream first so Spring's response close does not drain untrusted excess bytes.
    try (response;
        var body = response.getBody()) {
      int status = response.getStatusCode().value();
      if (status != 200) {
        throw new SpringBootVersionResolutionException(
            "adapter.springboot.resolution.http-status", phase, status);
      }
      byte[] bytes = body.readNBytes(limit + 1);
      if (bytes.length > limit) {
        throw new SpringBootVersionResolutionException("adapter.springboot.resolution.response-limit", phase);
      }
      return bytes;
    } catch (IOException e) {
      throw failed(phase, e);
    }
  }

  private static SpringBootVersionResolutionException failed(String phase, Throwable cause) {
    return new SpringBootVersionResolutionException(
        "adapter.springboot.resolution.phase-failed", cause, phase, cause.getMessage());
  }

  private static boolean isConnectionFailure(Throwable cause) {
    boolean unavailable = false;
    for (Throwable failure = cause; failure != null; failure = failure.getCause()) {
      if (failure instanceof SSLException) {
        return false;
      }
      unavailable |=
          failure instanceof HttpTimeoutException
              || failure instanceof ConnectException
              || failure instanceof NoRouteToHostException
              || failure instanceof UnknownHostException
              || failure instanceof UnresolvedAddressException;
    }
    return unavailable;
  }
}
