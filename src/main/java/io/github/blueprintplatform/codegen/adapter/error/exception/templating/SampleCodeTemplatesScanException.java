package io.github.blueprintplatform.codegen.adapter.error.exception.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class SampleCodeTemplatesScanException extends AdapterException {

  private static final String KEY = "adapter.sample-code.templates.scan.failed";

  public SampleCodeTemplatesScanException(String templateRoot, Throwable cause) {
    super(KEY, cause, templateRoot);
  }
}
