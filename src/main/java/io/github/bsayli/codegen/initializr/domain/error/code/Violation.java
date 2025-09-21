package io.github.bsayli.codegen.initializr.domain.error.code;

public enum Violation {
  NOT_BLANK(".not.blank"),
  LENGTH(".length"),
  INVALID_CHARS(".invalid.chars"),
  RESERVED(".reserved");
  public final String suffix;

  Violation(String s) {
    this.suffix = s;
  }
}
