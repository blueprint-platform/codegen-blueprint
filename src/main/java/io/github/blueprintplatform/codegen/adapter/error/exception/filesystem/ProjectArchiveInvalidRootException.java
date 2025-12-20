package io.github.blueprintplatform.codegen.adapter.error.exception.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import java.nio.file.Path;

public final class ProjectArchiveInvalidRootException extends AdapterException {

  private static final String KEY = "adapter.project.archive.invalid.root";

  public ProjectArchiveInvalidRootException(Path root) {
    super(KEY, root != null ? root : "<null>");
  }
}
