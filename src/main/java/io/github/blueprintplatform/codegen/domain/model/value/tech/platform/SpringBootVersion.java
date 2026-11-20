package io.github.blueprintplatform.codegen.domain.model.value.tech.platform;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.shared.KeyEnumParser;
import io.github.blueprintplatform.codegen.domain.shared.KeyedEnum;

public enum SpringBootVersion implements KeyedEnum {
  V4_2("4.2"),
  V4_1("4.1");

  private static final ErrorCode UNKNOWN = () -> "platform.springboot-version.unknown";

  private final String key;

  SpringBootVersion(String key) {
    this.key = key;
  }

  public static SpringBootVersion fromKey(String raw) {
    return KeyEnumParser.parse(SpringBootVersion.class, raw, UNKNOWN);
  }

  @Override
  public String key() {
    return key;
  }

  public String majorMinor() {
    return key;
  }

  public boolean matchesPatch(String patch) {
    return patch != null
        && patch.startsWith(key + ".")
        && patch.substring(key.length() + 1).matches("0|[1-9][0-9]{0,8}");
  }

  @Override
  public String toString() {
    return key;
  }
}
