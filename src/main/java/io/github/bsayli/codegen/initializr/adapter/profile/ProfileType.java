package io.github.bsayli.codegen.initializr.adapter.profile;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Language;

public enum ProfileType {
  SPRINGBOOT_MAVEN_JAVA(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);

  private final Framework framework;
  private final BuildTool buildTool;
  private final Language language;

  ProfileType(Framework framework, BuildTool buildTool, Language language) {
    this.framework = framework;
    this.buildTool = buildTool;
    this.language = language;
  }

  public static ProfileType from(BuildOptions o) {
    for (ProfileType p : values()) {
      if (p.framework == o.framework()
          && p.buildTool == o.buildTool()
          && p.language == o.language()) {
        return p;
      }
    }
    return null;
  }

  public String key() {
    return (framework.name() + "-" + buildTool.name() + "-" + language.name()).toLowerCase();
  }
}
