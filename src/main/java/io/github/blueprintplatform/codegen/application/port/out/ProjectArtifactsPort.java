package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;

public interface ProjectArtifactsPort {

  Iterable<GeneratedResource> generate(ProjectBlueprint blueprint);
}
