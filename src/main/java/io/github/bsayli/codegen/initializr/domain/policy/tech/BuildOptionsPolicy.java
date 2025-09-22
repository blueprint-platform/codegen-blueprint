package io.github.bsayli.codegen.initializr.domain.policy.tech;

import io.github.bsayli.codegen.initializr.domain.error.exception.DomainViolationException;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.BuildOptions;

public final class BuildOptionsPolicy {

  private BuildOptionsPolicy() {}

  public static BuildOptions enforce(BuildOptions options) {
    if (options == null
        || options.framework() == null
        || options.buildTool() == null
        || options.language() == null) {
      throw new DomainViolationException(() -> "project.build-options.not.blank");
    }
    return options;
  }
}
