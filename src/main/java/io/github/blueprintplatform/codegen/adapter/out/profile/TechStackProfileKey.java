package io.github.blueprintplatform.codegen.adapter.out.profile;

import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;

final class TechStackProfileKey {
  private TechStackProfileKey() {}

  static String from(TechStack techStack) {
    return techStack.framework().key()
        + "-"
        + techStack.buildTool().key()
        + "-"
        + techStack.language().key();
  }
}
