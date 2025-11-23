package io.github.bsayli.codegen.initializr.adapter.error.exception;

import io.github.bsayli.codegen.initializr.application.port.out.artifact.ArtifactKey;

@SuppressWarnings("java:S110")
public final class GeneratorFactoryNotFoundException extends AdapterException {
  private static final String KEY = "adapter.generator.factory.not.found";

  public GeneratorFactoryNotFoundException(ArtifactKey artifactKey) {
    super(KEY, artifactKey.key());
  }
}
