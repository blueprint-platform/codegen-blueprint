package io.github.bsayli.codegen.initializr.domain.model.value;

import io.github.bsayli.codegen.initializr.domain.policy.ProjectNamePolicy;

public record ProjectName(String value) {
  public ProjectName {
    value = ProjectNamePolicy.enforce(value);
  }
}
