package io.github.blueprintplatform.codegen.adapter.error.exception.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public final class SpringBootResolutionException extends AdapterException {
  public SpringBootResolutionException(String detailKey, Object... args) {
    this(detailKey, null, args);
  }

  public SpringBootResolutionException(String detailKey, Throwable cause, Object... args) {
    super(
        "adapter.springboot.resolution.failed",
        cause,
        new DefaultMessageSourceResolvable(new String[] {detailKey}, args, detailKey));
  }
}
