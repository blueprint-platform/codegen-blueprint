package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

public enum SpringBootArchitectureEnforcementOption implements CliKeyedEnum {
  NONE,
  BASIC,
  STRICT;

  private static final String UNKNOWN_KEY =
      "adapter.cli.springboot.architecture.enforcement.unknown";

  public static SpringBootArchitectureEnforcementOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootArchitectureEnforcementOption.class, raw, UNKNOWN_KEY);
  }

  @Override
  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  @Override
  public String toString() {
    return key();
  }
}
