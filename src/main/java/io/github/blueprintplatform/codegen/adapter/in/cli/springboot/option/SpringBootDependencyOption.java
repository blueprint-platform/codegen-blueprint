package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.error.exception.cli.InvalidDependencyAliasException;

public enum SpringBootDependencyOption {
  WEB(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-starter-web"),
  DATA_JPA(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-starter-data-jpa"),
  VALIDATION(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-starter-validation"),
  ACTUATOR(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-starter-actuator"),
  SECURITY(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-starter-security"),
  DEVTOOLS(Constants.ORG_SPRINGFRAMEWORK_BOOT, "spring-boot-devtools");

  private final String groupId;
  private final String artifactId;

  SpringBootDependencyOption(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
  }

  public static SpringBootDependencyOption fromKey(String raw) {
    if (raw == null || raw.isBlank()) {
      throw new InvalidDependencyAliasException(String.valueOf(raw));
    }

    String normalized = raw.trim();

    for (SpringBootDependencyOption alias : values()) {
      if (alias.name().equalsIgnoreCase(normalized)) {
        return alias;
      }
    }
    throw new InvalidDependencyAliasException(raw);
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  private static class Constants {
    public static final String ORG_SPRINGFRAMEWORK_BOOT = "org.springframework.boot";
  }
}
