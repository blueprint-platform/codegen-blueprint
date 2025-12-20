package io.github.blueprintplatform.codegen.adapter.out.profile;

import io.github.blueprintplatform.codegen.adapter.error.exception.artifact.ArtifactsPortNotFoundException;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.Map;

public class ProfileBasedArtifactsSelector implements ProjectArtifactsSelector {

  private final Map<String, ProjectArtifactsPort> registry;

  public ProfileBasedArtifactsSelector(Map<String, ProjectArtifactsPort> registry) {
    this.registry = registry;
  }

  @Override
  public ProjectArtifactsPort select(TechStack options) {
    String key = TechStackProfileKey.from(options);

    ProjectArtifactsPort port = registry.get(key);
    if (port == null) {
      throw new ArtifactsPortNotFoundException(key);
    }
    return port;
  }
}
