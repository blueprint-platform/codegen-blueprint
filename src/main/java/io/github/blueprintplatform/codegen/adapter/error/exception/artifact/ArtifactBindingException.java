package io.github.blueprintplatform.codegen.adapter.error.exception.artifact;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class ArtifactBindingException extends AdapterException {

  public ArtifactBindingException(String messageKey, String artifactKey) {
    super(messageKey, artifactKey);
  }
}
