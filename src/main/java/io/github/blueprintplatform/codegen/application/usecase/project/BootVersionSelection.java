package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.port.in.project.model.BootResolution;
import io.github.blueprintplatform.codegen.application.port.in.project.model.BootResolution.Source;
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
    for (var line : SpringBootVersion.values()) {
      line.validatePatch(bundled.get(line));
    }
    this.bundled = Map.copyOf(bundled);
  }

  public BootResolution select(SpringBootVersion line) {
    try {
      return new BootResolution(resolver.resolveLatest(line), Source.CENTRAL_LATEST, null);
    } catch (SpringBootRepositoryUnavailableException e) {
      return new BootResolution(bundled.get(line), Source.BUNDLED_FALLBACK, e.getMessage());
    }
  }
}
