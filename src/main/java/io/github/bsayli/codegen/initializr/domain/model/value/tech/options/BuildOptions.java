package io.github.bsayli.codegen.initializr.domain.model.value.tech.options;

import io.github.bsayli.codegen.initializr.domain.policy.tech.BuildOptionsPolicy;

public record BuildOptions(Framework framework, BuildTool buildTool, Language language) {
  public BuildOptions {
    BuildOptionsPolicy.enforce(this);
  }
}
