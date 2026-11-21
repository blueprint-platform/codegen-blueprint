package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;

public interface ProjectTargetResolver {
  /** Resolves a validated request while preserving its selected platform and runtime line. */
  TargetResolution resolve(PlatformTarget requested);
}
