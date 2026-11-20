package io.github.blueprintplatform.codegen.domain.model.value.tech.platform;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;

public record SpringBootJvmTarget(
    JavaVersion java, SpringBootVersion springBoot, String patchVersion) implements PlatformTarget {

  private static final ErrorCode TARGET_REQUIRED = () -> "platform.target.not.blank";

  public SpringBootJvmTarget(JavaVersion java, SpringBootVersion springBoot) {
    this(java, springBoot, null);
  }

  public SpringBootJvmTarget {
    if (java == null || springBoot == null) {
      throw new DomainViolationException(TARGET_REQUIRED);
    }
    if (patchVersion != null && !springBoot.matchesPatch(patchVersion)) {
      throw new DomainViolationException(() -> "platform.springboot-patch.invalid", patchVersion);
    }
  }
}
