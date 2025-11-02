package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.source;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.SourceScaffolderPort;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;

public final class SourceScaffolderAdapter implements SourceScaffolderPort {

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    throw new UnsupportedOperationException("SourceScaffolderAdapter.generate not implemented yet");
  }

  @Override
  public ArtifactKey artifactKey() {
    return null;
  }
}
