package io.github.blueprintplatform.codegen.domain.port.out.artifact;

import static io.github.blueprintplatform.codegen.domain.policy.file.GeneratedFilePolicy.*;

import java.nio.file.Path;

public record GeneratedDirectory(Path relativePath) implements GeneratedResource {

  public GeneratedDirectory {
    requireRelativePath(relativePath);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString() {
    return "GeneratedDirectory[" + relativePath + "]";
  }
}
