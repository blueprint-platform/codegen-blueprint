package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.wrapper;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.out.spi.ArtifactGenerator;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.MavenWrapperPort;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;

public final class MavenWrapperAdapter implements MavenWrapperPort, ArtifactGenerator {

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    throw new UnsupportedOperationException("MavenWrapperAdapter.generate not implemented yet");
  }

  @Override
  public ArtifactKey artifactKey() {
    return null;
  }

  @Override
  public Iterable<? extends GeneratedFile> generateFiles(ProjectBlueprint blueprint) {
    return generate(blueprint);
  }
}
