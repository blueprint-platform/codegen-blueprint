package io.github.blueprintplatform.codegen.adapter.error.exception.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public class TemplateScanException extends AdapterException {

  private static final String KEY = "adapter.template.scan.failed";

  public TemplateScanException(String templateRoot, Throwable cause) {
    super(KEY, cause, templateRoot);
  }
}
