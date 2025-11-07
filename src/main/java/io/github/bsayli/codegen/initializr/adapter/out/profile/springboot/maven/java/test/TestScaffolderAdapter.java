package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.test;

import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.TestScaffolderPort;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;

public final class TestScaffolderAdapter implements TestScaffolderPort {
  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    throw new UnsupportedOperationException("TestScaffolderAdapter.generate not implemented yet");
  }

  @Override
  public ArtifactKey artifactKey() {
    return null;
  }
}
