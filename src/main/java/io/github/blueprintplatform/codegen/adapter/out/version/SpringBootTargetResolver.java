package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Source;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.util.Map;

public final class SpringBootTargetResolver implements ProjectTargetResolver {

  private final CentralSpringBootPatchResolver resolver;
  private final Map<SpringBootVersion, String> bundled;

  public SpringBootTargetResolver(
      CentralSpringBootPatchResolver resolver, Map<SpringBootVersion, String> bundled) {
    this.resolver = resolver;
    for (var line : SpringBootVersion.values()) {
      line.validatePatch(bundled.get(line));
    }
    this.bundled = Map.copyOf(bundled);
  }

  @Override
  public TargetResolution resolve(PlatformTarget requested) {
    var target = (SpringBootJvmTarget) requested;
    var line = target.springBoot();
    try {
      var resolved = new SpringBootJvmTarget(target.java(), line, resolver.resolveLatest(line));
      return new TargetResolution(resolved, Source.REMOTE_LATEST, null);
    } catch (SpringBootRepositoryUnavailableException e) {
      var resolved = new SpringBootJvmTarget(target.java(), line, bundled.get(line));
      return new TargetResolution(resolved, Source.BUNDLED_FALLBACK, e.warning());
    }
  }
}
