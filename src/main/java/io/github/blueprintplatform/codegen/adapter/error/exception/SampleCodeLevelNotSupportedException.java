package io.github.blueprintplatform.codegen.adapter.error.exception;

public final class SampleCodeLevelNotSupportedException extends AdapterException {

  private static final String KEY = "adapter.sample-code.level.unsupported";

  public SampleCodeLevelNotSupportedException(String levelKey) {
    super(KEY, levelKey);
  }
}
