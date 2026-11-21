package io.github.blueprintplatform.codegen.adapter.error.exception.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class MavenCentralUnavailableException extends AdapterException {
  public MavenCentralUnavailableException(String phase, Throwable cause) {
    super("adapter.springboot.repository.unavailable", cause, phase, cause.getClass().getSimpleName());
  }
}
