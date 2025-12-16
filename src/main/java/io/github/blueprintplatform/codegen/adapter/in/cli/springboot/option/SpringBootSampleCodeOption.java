package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

public enum SpringBootSampleCodeOption implements CliKeyedEnum {
  NONE,
  BASIC;

  private static final String UNKNOWN_KEY = "adapter.cli.springboot.sample-code.unknown";

  public static SpringBootSampleCodeOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootSampleCodeOption.class, raw, UNKNOWN_KEY);
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
