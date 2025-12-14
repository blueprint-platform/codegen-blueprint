package io.github.blueprintplatform.codegen.adapter.error.exception;

import java.nio.file.Path;

public final class ProjectRootIOException extends AdapterException {

  private static final String KEY = "adapter.project-root.io.failed";

  public ProjectRootIOException(Path path, Throwable cause) {
    super(KEY, cause, path);
  }
}
