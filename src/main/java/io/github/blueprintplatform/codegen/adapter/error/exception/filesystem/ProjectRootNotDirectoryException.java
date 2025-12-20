package io.github.blueprintplatform.codegen.adapter.error.exception.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import java.nio.file.Path;

public final class ProjectRootNotDirectoryException extends AdapterException {

  private static final String KEY = "adapter.project-root.not-directory";

  public ProjectRootNotDirectoryException(Path path) {
    super(KEY, path);
  }
}
