package io.github.bsayli.codegen.initializr.domain.model.value;

import io.github.bsayli.codegen.initializr.domain.policy.GroupIdPolicy;

public record GroupId(String value) {
  public GroupId {
    value = GroupIdPolicy.enforce(value);
  }
}
