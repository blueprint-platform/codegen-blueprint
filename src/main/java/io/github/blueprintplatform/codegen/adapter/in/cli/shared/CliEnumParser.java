package io.github.blueprintplatform.codegen.adapter.in.cli.shared;

import io.github.blueprintplatform.codegen.adapter.error.exception.InvalidCliOptionException;
import java.util.Locale;

public final class CliEnumParser {

  private CliEnumParser() {}

  public static <E extends Enum<E> & CliKeyedEnum> E parse(
      Class<E> type, String raw, String messageKey) {

    if (raw == null || raw.isBlank()) {
      throw new InvalidCliOptionException(messageKey, String.valueOf(raw));
    }

    String normalized = raw.trim().toLowerCase(Locale.ROOT);

    for (E e : type.getEnumConstants()) {
      if (e.key().equalsIgnoreCase(normalized)) {
        return e;
      }
    }

    throw new InvalidCliOptionException(messageKey, normalized);
  }
}
