package io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option;

import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliEnumParser;
import io.github.blueprintplatform.codegen.adapter.in.cli.shared.CliKeyedEnum;
import java.util.Locale;

public enum SpringBootLayoutOption implements CliKeyedEnum {
    STANDARD,
    HEXAGONAL;

    private static final String UNKNOWN_KEY = "adapter.cli.springboot.layout.unknown";

    public static SpringBootLayoutOption fromKey(String raw) {
        return CliEnumParser.parse(SpringBootLayoutOption.class, raw, UNKNOWN_KEY);
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