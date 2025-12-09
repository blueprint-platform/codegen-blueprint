package io.github.blueprintplatform.codegen.application.port.out.artifact;

import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;

public interface ArtifactPort {
  ArtifactKey artifactKey();

  Iterable<? extends GeneratedResource> generate(ProjectBlueprint blueprint);
}
