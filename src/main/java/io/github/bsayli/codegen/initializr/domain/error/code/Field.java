package io.github.bsayli.codegen.initializr.domain.error.code;

public enum Field implements ErrorCode {
  PROJECT_NAME("project.name"),
  PROJECT_DESCRIPTION("project.description");

  private final String key;

  Field(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
