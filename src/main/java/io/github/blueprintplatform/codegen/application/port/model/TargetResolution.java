package io.github.blueprintplatform.codegen.application.port.model;

import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import java.util.List;

public record TargetResolution(PlatformTarget target, Source source, Warning warning) {
  /** Message data for the caller to render in its presentation layer. */
  public record Warning(String messageKey, List<Object> arguments) {
    public Warning {
      arguments = List.copyOf(arguments);
    }
  }

  public enum Source {
    REMOTE_LATEST,
    BUNDLED_FALLBACK
  }
}
