package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;

public enum SpringBootVersionOption implements CliKeyedEnum {
  V3_5("3.5"),
  V3_4("3.4");

  private static final String UNKNOWN_KEY = "adapter.cli.springboot.spring-boot-version.unknown";

  private final String value;

  SpringBootVersionOption(String value) {
    this.value = value;
  }

  public static SpringBootVersionOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootVersionOption.class, raw, UNKNOWN_KEY);
  }

  public String value() {
    return value;
  }

  @Override
  public String key() {
    return value;
  }

  @Override
  public String toString() {
    return key();
  }
}
