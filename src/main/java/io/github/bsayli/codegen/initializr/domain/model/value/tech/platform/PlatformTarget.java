package io.github.bsayli.codegen.initializr.domain.model.value.tech.platform;

import io.github.bsayli.codegen.initializr.domain.error.code.ErrorCode;
import io.github.bsayli.codegen.initializr.domain.error.exception.DomainViolationException;

public record PlatformTarget(JavaVersion java, SpringBootVersion springBoot) {
  private static final ErrorCode TARGET_REQUIRED = () -> "platform.target.not.blank";

  public PlatformTarget {
    if (java == null || springBoot == null) {
      throw new DomainViolationException(TARGET_REQUIRED);
    }
  }
}
