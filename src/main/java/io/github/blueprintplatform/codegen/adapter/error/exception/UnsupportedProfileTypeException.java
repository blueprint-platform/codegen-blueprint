package io.github.blueprintplatform.codegen.adapter.error.exception;

import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;

public final class UnsupportedProfileTypeException extends AdapterException {
  private static final String KEY = "adapter.profile.unsupported";

  public UnsupportedProfileTypeException(TechStack options) {
    super(KEY, options.framework(), options.buildTool(), options.language());
  }
}
