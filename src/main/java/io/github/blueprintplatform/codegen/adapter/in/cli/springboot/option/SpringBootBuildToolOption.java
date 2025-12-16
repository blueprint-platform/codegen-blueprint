package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

@SuppressWarnings("java:S6548")
public enum SpringBootBuildToolOption implements CliKeyedEnum {
  MAVEN;

  private static final String UNKNOWN_KEY = "adapter.cli.springboot.build-tool.unknown";

  public static SpringBootBuildToolOption fromKey(String raw) {
    return CliEnumParser.parse(SpringBootBuildToolOption.class, raw, UNKNOWN_KEY);
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
