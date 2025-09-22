package io.github.bsayli.codegen.initializr.domain.model.value;

import io.github.bsayli.codegen.initializr.domain.policy.ArtifactIdPolicy;

public record ArtifactId(String value) {
  public ArtifactId {
    value = ArtifactIdPolicy.enforce(value);
  }
}
