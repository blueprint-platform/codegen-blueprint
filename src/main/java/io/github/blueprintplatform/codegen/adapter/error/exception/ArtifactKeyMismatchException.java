package io.github.blueprintplatform.codegen.adapter.error.exception;

import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;

public final class ArtifactKeyMismatchException extends AdapterException {
  private static final String KEY = "adapter.generator.key.mismatch";

  public ArtifactKeyMismatchException(ArtifactKey expected, String artifactKey) {
    super(KEY, expected.key(), artifactKey);
  }
}
