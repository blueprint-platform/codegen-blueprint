package io.github.bsayli.codegen.initializr.domain.policy.tech;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.JavaVersion;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.PlatformTarget;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.SpringBootVersion;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import java.util.List;

public final class PlatformTargetSelector {

  private PlatformTargetSelector() {}

  public static PlatformTarget select(
      BuildOptions options, JavaVersion preferredJava, SpringBootVersion preferredBoot) {
    var requested = new PlatformTarget(preferredJava, preferredBoot);
    CompatibilityPolicy.ensureCompatible(options, requested);
    return requested;
  }

  @SuppressWarnings("unused")
  public static List<PlatformTarget> supportedTargetsFor(BuildOptions options) {
    return CompatibilityPolicy.allSupportedTargets();
  }
}
