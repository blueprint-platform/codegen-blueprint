package io.github.bsayli.codegen.initializr.adapter.out.spi;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;

public interface ArtifactGenerator {

  ArtifactKey artifactKey();

  Iterable<? extends GeneratedFile> generateFiles(ProjectBlueprint blueprint);

  default String name() {
    return getClass().getSimpleName();
  }
}
