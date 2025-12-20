package io.github.blueprintplatform.codegen.adapter.error.exception.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import java.nio.file.Path;

public final class ProjectRootAlreadyExistsException extends AdapterException {

  private static final String KEY = "adapter.project-root.already-exists";

  public ProjectRootAlreadyExistsException(Path path) {
    super(KEY, path);
  }
}
