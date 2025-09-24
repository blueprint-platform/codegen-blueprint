package io.github.bsayli.codegen.initializr.domain.port.out;

import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;

public interface MavenPomPort {
  GeneratedFile generate(ProjectBlueprint blueprint);
}
