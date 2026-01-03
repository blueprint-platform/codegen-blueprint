package io.github.blueprintplatform.codegen.adapter.error.exception.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class ArchitectureGovernanceTemplatesNotFoundException extends AdapterException {

  private static final String KEY = "adapter.architecture-governance.templates.not-found";

  public ArchitectureGovernanceTemplatesNotFoundException(
      String templateRoot, String layoutKey, String guardrailsModeKey) {
    super(KEY, templateRoot, layoutKey, guardrailsModeKey);
  }
}
