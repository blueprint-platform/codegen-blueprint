package io.github.blueprintplatform.codegen.adapter.error.exception.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class InvalidMavenMetadataException extends AdapterException {
  public InvalidMavenMetadataException(String field) {
    super("adapter.pom.metadata.invalid", field);
  }
}
