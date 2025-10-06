package io.github.bsayli.codegen.initializr.adapter.error.exception;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;

@SuppressWarnings("java:S110")
public final class UnsupportedProfileTypeException extends AdapterException {
  private static final String KEY = "adapter.profile.unsupported";

  public UnsupportedProfileTypeException(BuildOptions options) {
    super(KEY, options.framework(), options.buildTool(), options.language());
  }
}
