package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

final class SpringBootRepositoryUnavailableException extends AdapterException {
  private final String phase;

  SpringBootRepositoryUnavailableException(String phase, Throwable cause) {
    super("adapter.springboot.repository.unavailable", cause, phase);
    this.phase = phase;
  }

  String warning() {
    return phase + ": " + getCause().getClass().getSimpleName();
  }
}
