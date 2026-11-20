package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;

public enum SpringBootVersionOption implements CliKeyedEnum {
  V4_2("4.2"),
  V4_1("4.1");

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
