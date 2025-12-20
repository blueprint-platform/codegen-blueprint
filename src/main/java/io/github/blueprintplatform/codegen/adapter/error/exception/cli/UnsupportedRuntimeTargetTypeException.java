package io.github.blueprintplatform.codegen.adapter.error.exception.cli;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class UnsupportedRuntimeTargetTypeException extends AdapterException {

  private static final String KEY = "adapter.cli.runtime-target.type.unsupported";

  public UnsupportedRuntimeTargetTypeException(String type) {
    super(KEY, type);
  }
}
