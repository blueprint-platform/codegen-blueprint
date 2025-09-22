package io.github.bsayli.codegen.initializr.domain.model.value;

import io.github.bsayli.codegen.initializr.domain.policy.PackageNamePolicy;

public record PackageName(String value) {
  public PackageName {
    value = PackageNamePolicy.enforce(value);
  }
}
