package io.github.bsayli.codegen.initializr.domain.error.code;

public enum Field implements ErrorCode {
  PROJECT_NAME(project("name")),
  PROJECT_DESCRIPTION(project("description")),
  GROUP_ID(project("group-id")),
  ARTIFACT_ID(project("artifact-id")),
  PACKAGE_NAME(project("package-id"));

  private final String key;

  Field(String key) {
    this.key = key;
  }

  private static String project(String suffix) {
    return "project." + suffix;
  }

  @Override
  public String key() {
    return key;
  }
}
