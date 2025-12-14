package io.github.blueprintplatform.codegen.application.exception;

public final class InvalidArtifactKeyException extends ApplicationException {

  private static final String KEY = "application.artifact.key.unknown";

  public InvalidArtifactKeyException(String key) {
    super(KEY, key);
  }
}
