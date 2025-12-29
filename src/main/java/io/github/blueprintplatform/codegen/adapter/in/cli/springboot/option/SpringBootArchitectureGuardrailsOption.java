package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

public enum SpringBootArchitectureGuardrailsOption implements CliKeyedEnum {
  NONE,
  BASIC,
  STRICT;

  private static final String UNKNOWN_KEY =
      "adapter.cli.springboot.architecture.guardrails.unknown";

  public static SpringBootArchitectureGuardrailsOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootArchitectureGuardrailsOption.class, raw, UNKNOWN_KEY);
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
