package io.github.blueprintplatform.codegen.adapter.error.exception.sample;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class SampleCodeLevelNotSupportedException extends AdapterException {

  private static final String KEY = "adapter.sample-code.level.unsupported";

  public SampleCodeLevelNotSupportedException(String levelKey) {
    super(KEY, levelKey);
  }
}
