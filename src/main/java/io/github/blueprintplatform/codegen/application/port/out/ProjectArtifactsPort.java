package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;

public interface ProjectArtifactsPort {

  /** Checks profile-specific metadata constraints before resolving the target. */
  default void validateMetadata(ProjectMetadata metadata) {}

  Iterable<GeneratedResource> generate(ProjectBlueprint blueprint);
}
