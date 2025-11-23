package io.github.bsayli.codegen.initializr.adapter.error.exception;

import io.github.bsayli.codegen.initializr.bootstrap.error.exception.InfrastructureException;

@SuppressWarnings("java:S110")
public final class UnknownArtifactKeyException extends InfrastructureException {
  private static final String KEY = "adapter.artifact.key.unknown";

  public UnknownArtifactKeyException(String key) {
    super(KEY, key);
  }
}
