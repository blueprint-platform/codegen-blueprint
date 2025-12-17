  package io.github.blueprintplatform.codegen.domain.model.value.sample;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.shared.KeyEnumParser;
import io.github.blueprintplatform.codegen.domain.shared.KeyedEnum;

public enum SampleCodeLevel implements KeyedEnum {
    NONE("none"),
    BASIC("basic"),
    RICH("rich");

    private static final ErrorCode UNKNOWN = () -> "project.sample.level.unknown";

    private final String key;

    SampleCodeLevel(String key) {
      this.key = key;
    }

    public static SampleCodeLevel fromKey(String rawKey) {
      return KeyEnumParser.parse(SampleCodeLevel.class, rawKey, UNKNOWN);
    }

    public boolean isEnabled() {
      return this != NONE;
    }

    @Override
    public String key() {
      return key;
    }

    @Override
    public String toString() {
      return key;
    }
  }
