package io.github.bsayli.codegen.initializr.domain.policy.tech;

import io.github.bsayli.codegen.initializr.domain.error.exception.DomainViolationException;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Language;

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

  public static void requireNonNull(Framework framework, BuildTool buildTool, Language language) {
    if (framework == null || buildTool == null || language == null) {
      throw new DomainViolationException(() -> "project.build-options.not.blank");
    }
  }
}
