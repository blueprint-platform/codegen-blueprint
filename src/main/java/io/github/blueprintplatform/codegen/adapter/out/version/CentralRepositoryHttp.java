package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootResolutionException;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootRepositoryUnavailableException;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

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
    HttpResponse<byte[]> result;
    try {
      result = response.get(remaining, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      response.cancel(true);
      Thread.currentThread().interrupt();
      throw new SpringBootResolutionException("Central lookup interrupted", e);
    } catch (ExecutionException | TimeoutException e) {
      response.cancel(true);
      Throwable cause = e instanceof ExecutionException ? e.getCause() : e;
      if (!headersReceived.get() && isConnectionFailure(cause)) {
        throw new SpringBootRepositoryUnavailableException(phase, cause);
      }
      throw failed(phase, cause);
    }
    if (result.statusCode() != 200) {
      throw failed(
          phase, new IOException("Central " + phase + " returned HTTP " + result.statusCode()));
    }
    return result.body();
  }

  private static SpringBootResolutionException failed(String phase, Throwable cause) {
    return new SpringBootResolutionException(
        "Central " + phase + " failed: " + cause.getMessage(), cause);
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
