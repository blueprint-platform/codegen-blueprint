package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.port.out.SpringBootPatchResolver;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootRepositoryUnavailableException;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.util.Map;

public final class BootVersionSelection {

  private final SpringBootPatchResolver resolver;
  private final Map<SpringBootVersion, String> bundled;

  public BootVersionSelection(
      SpringBootPatchResolver resolver, Map<SpringBootVersion, String> bundled) {
    this.resolver = resolver;
    this.bundled = Map.copyOf(bundled);
    for (var line : SpringBootVersion.values()) {
      if (!line.matchesPatch(this.bundled.get(line))) {
        throw new IllegalArgumentException("Invalid bundled Spring Boot patch for " + line);
      }
    }
  }

  public Result select(SpringBootVersion line) {
    try {
      return new Result(resolver.resolveLatest(line), "CENTRAL_LATEST", null);
    } catch (SpringBootRepositoryUnavailableException e) {
      return new Result(bundled.get(line), "BUNDLED_FALLBACK", e.getMessage());
    }
  }

  public record Result(String patch, String source, String warning) {}
}
