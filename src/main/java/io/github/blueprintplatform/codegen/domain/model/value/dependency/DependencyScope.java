package io.github.blueprintplatform.codegen.domain.model.value.dependency;

import io.github.blueprintplatform.codegen.domain.shared.KeyEnumParser;
import io.github.blueprintplatform.codegen.domain.shared.KeyedEnum;

public enum DependencyScope implements KeyedEnum {
  COMPILE("compile"),
  PROVIDED("provided"),
  RUNTIME("runtime"),
  TEST("test"),
  SYSTEM("system"),
  IMPORT("import");

  private final String value;

  DependencyScope(String value) {
    this.value = value;
  }

  public static DependencyScope fromOptionalKey(String raw) {
    return raw == null || raw.isBlank()
        ? null
        : KeyEnumParser.parse(DependencyScope.class, raw, () -> "dependency.scope.unknown");
  }

  @Override
  public String key() {
    return value;
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
