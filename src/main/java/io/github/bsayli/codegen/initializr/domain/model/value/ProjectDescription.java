package io.github.bsayli.codegen.initializr.domain.model.value;

import io.github.bsayli.codegen.initializr.domain.policy.ProjectDescriptionPolicy;

public record ProjectDescription(String value) {
  public ProjectDescription {
    value = ProjectDescriptionPolicy.enforce(value);
  }

  public boolean isEmpty() {
    return value.isEmpty();
  }
}
