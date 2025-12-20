package io.github.blueprintplatform.codegen.adapter.error.exception.sample;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class SampleCodeTemplatesNotFoundException extends AdapterException {

  private static final String KEY = "adapter.sample-code.templates.not-found";

  public SampleCodeTemplatesNotFoundException(
      String templateRoot, String layoutKey, String levelKey) {
    super(KEY, templateRoot, layoutKey, levelKey);
  }
}
