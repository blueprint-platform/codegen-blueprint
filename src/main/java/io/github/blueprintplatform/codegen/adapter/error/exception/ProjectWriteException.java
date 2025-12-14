package io.github.blueprintplatform.codegen.adapter.error.exception;

import java.nio.file.Path;

public final class ProjectWriteException extends AdapterException {

  private static final String KEY = "adapter.project.write.failed";

  public ProjectWriteException(Path path, Throwable cause) {
    super(KEY, cause, path);
  }
}
