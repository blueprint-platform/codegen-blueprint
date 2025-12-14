package io.github.blueprintplatform.codegen.adapter.error.exception;

import java.nio.file.Path;

public final class ProjectFileListingException extends AdapterException {

  private static final String KEY = "adapter.project-file.listing.failed";

  public ProjectFileListingException(Path root, Throwable cause) {
    super(KEY, cause, root);
  }
}
