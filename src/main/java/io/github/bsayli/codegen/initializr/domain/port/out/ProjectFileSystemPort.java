package io.github.bsayli.codegen.initializr.domain.port.out;

import java.nio.charset.Charset;
import java.nio.file.Path;

public interface ProjectFileSystemPort {

  Path prepareProjectRoot(Path targetDirectory, String artifactId, OnExistsPolicy policy);

  void writeBytes(Path projectRoot, Path relativePath, byte[] content);

  void writeText(Path projectRoot, Path relativePath, CharSequence content, Charset charset);

  enum OnExistsPolicy {
    FAIL_IF_EXISTS,
    OVERWRITE
  }
}
