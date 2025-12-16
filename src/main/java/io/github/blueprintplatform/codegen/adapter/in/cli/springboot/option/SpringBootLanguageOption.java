package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

@SuppressWarnings("java:S6548")
public enum SpringBootLanguageOption implements CliKeyedEnum {
  JAVA;

  private static final String UNKNOWN_KEY = "adapter.cli.springboot.language.unknown";

  public static SpringBootLanguageOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootLanguageOption.class, raw, UNKNOWN_KEY);
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
