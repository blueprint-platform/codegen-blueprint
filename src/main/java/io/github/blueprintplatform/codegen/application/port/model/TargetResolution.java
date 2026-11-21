package io.github.blueprintplatform.codegen.application.port.model;

import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;

public record TargetResolution(PlatformTarget target, Source source, String warning) {
  public enum Source {
    REMOTE_LATEST,
    BUNDLED_FALLBACK
  }
}
