package io.github.blueprintplatform.codegen.adapter.error.exception;

import java.nio.file.Path;

public final class ProjectOutputDiscoveryException extends AdapterException {

  private static final String KEY = "adapter.project-output.discovery.failed";

  public ProjectOutputDiscoveryException(Path root, Throwable cause) {
    super(KEY, cause, root);
  }
}
