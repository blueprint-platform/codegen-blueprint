package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.MavenCentralUnavailableException;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Source;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Warning;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.Map;

public final class SpringBootTargetResolver implements ProjectTargetResolver {

  private final MavenCentralSpringBootPatchLookup patchLookup;
  private final Map<SpringBootVersion, String> bundled;

  public SpringBootTargetResolver(
      HttpClient client, URI parentDirectory, Map<SpringBootVersion, String> bundled) {
    this.patchLookup =
        new MavenCentralSpringBootPatchLookup(new MavenCentralHttpClient(client, parentDirectory));
    for (var line : SpringBootVersion.values()) {
      line.validatePatch(bundled.get(line));
    }
    this.bundled = Map.copyOf(bundled);
  }

  @Override
  public TargetResolution resolve(PlatformTarget requested) {
    return switch (requested) {
      case SpringBootJvmTarget target -> resolveSpringBoot(target);
    };
  }

  private TargetResolution resolveSpringBoot(SpringBootJvmTarget target) {
    var line = target.springBoot();
    String patch;
    try {
      patch = patchLookup.findLatestPatch(line);
    } catch (MavenCentralUnavailableException e) {
      var resolved = new SpringBootJvmTarget(target.java(), line, bundled.get(line));
      var warning = new Warning(e.getMessageKey(), Arrays.asList(e.getArgs()));
      return new TargetResolution(resolved, Source.BUNDLED_FALLBACK, warning);
    }
    var resolved = new SpringBootJvmTarget(target.java(), line, patch);
    return new TargetResolution(resolved, Source.REMOTE_LATEST, null);
  }
}
