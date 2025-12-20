package io.github.blueprintplatform.codegen.adapter.error.exception.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class TemplateRenderingException extends AdapterException {
  private static final String KEY = "adapter.template.render.failed";
  private final String templateName;

  public TemplateRenderingException(String templateName, Object... args) {
    super(KEY, prepend(templateName, args));
    this.templateName = templateName;
  }

  public TemplateRenderingException(String templateName, Throwable cause, Object... args) {
    super(KEY, cause, prepend(templateName, args));
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return templateName;
  }
}
