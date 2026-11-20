package io.github.blueprintplatform.codegen.adapter.error.exception.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class SpringBootResolutionException extends AdapterException {
  public SpringBootResolutionException(String detail) {
    this(detail, null);
  }

  public SpringBootResolutionException(String detail, Throwable cause) {
    super("adapter.springboot.resolution.failed", cause, detail);
  }
}
