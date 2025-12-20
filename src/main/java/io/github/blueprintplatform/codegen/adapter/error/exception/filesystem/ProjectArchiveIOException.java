package io.github.blueprintplatform.codegen.adapter.error.exception.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import java.nio.file.Path;

public final class ProjectArchiveIOException extends AdapterException {

  private static final String KEY = "adapter.project.archive.io";

  public ProjectArchiveIOException(Path root, Throwable cause) {
    super(KEY, cause, root);
  }
}
