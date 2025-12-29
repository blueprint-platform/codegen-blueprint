package io.github.blueprintplatform.codegen.domain.model.value.architecture;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.shared.KeyEnumParser;
import io.github.blueprintplatform.codegen.domain.shared.KeyedEnum;

public enum GuardrailsMode implements KeyedEnum {
  NONE("none"),
  BASIC("basic"),
  STRICT("strict");

  private static final ErrorCode UNKNOWN = () -> "project.architecture.guardrails.unknown";

  private final String key;

  GuardrailsMode(String key) {
    this.key = key;
  }

  public static GuardrailsMode fromKey(String rawKey) {
    return KeyEnumParser.parse(GuardrailsMode.class, rawKey, UNKNOWN);
  }

  public boolean isEnabled() {
    return this != NONE;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public String toString() {
    return key;
  }
}
