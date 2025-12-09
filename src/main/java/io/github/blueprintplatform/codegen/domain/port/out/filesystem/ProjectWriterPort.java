package io.github.blueprintplatform.codegen.domain.port.out.filesystem;

import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedBinaryResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface ProjectWriterPort {

  void writeBytes(Path projectRoot, Path relativePath, byte[] content);

  void writeText(Path projectRoot, Path relativePath, String content, Charset charset);

  void createDirectories(Path projectRoot, Path relativeDir);

  default void writeText(Path root, Path relative, String content) {
    writeText(root, relative, content, java.nio.charset.StandardCharsets.UTF_8);
  }

  default void write(Path projectRoot, GeneratedResource resource) {
    switch (resource) {
      case GeneratedTextResource(Path p, String c, Charset cs) -> writeText(projectRoot, p, c, cs);
      case GeneratedBinaryResource(Path p, byte[] b) -> writeBytes(projectRoot, p, b);
      case GeneratedDirectory(Path p) -> createDirectories(projectRoot, p);
    }
  }

  default void write(Path projectRoot, Iterable<? extends GeneratedResource> resources) {
    for (GeneratedResource f : resources) write(projectRoot, f);
  }

  default void write(Path projectRoot, GeneratedResource... files) {
    for (GeneratedResource f : files) write(projectRoot, f);
  }

  default void write(Path projectRoot, java.util.stream.Stream<? extends GeneratedResource> files) {
    files.forEach(f -> write(projectRoot, f));
  }
}
