package io.github.blueprintplatform.codegen.application.port.out;

public final class SpringBootRepositoryUnavailableException extends RuntimeException {
  public SpringBootRepositoryUnavailableException(String phase, Throwable cause) {
    super(phase + ": " + cause.getClass().getSimpleName(), cause);
  }
}
