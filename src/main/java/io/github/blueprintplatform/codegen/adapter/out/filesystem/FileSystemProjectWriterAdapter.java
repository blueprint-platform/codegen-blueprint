package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedBinaryResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileSystemProjectWriterAdapter {

  void write(Path projectRoot, Iterable<? extends GeneratedResource> resources) {
    for (var resource : resources) {
      switch (resource) {
        case GeneratedBinaryResource(var path, var content) ->
            writeBytes(projectRoot, path, content.bytes());
        case GeneratedTextResource(var path, var content, var charset) ->
            writeText(projectRoot, path, content, charset);
        case GeneratedDirectory(var path) -> createDirectories(projectRoot, path);
      }
    }
  }

  void writeBytes(Path projectRoot, Path relativePath, byte[] content) {
    Path target = projectRoot.resolve(relativePath);
    try {
      Path parent = target.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      Files.write(target, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new ProjectWriteException(target, e);
    }
  }

  void writeText(Path projectRoot, Path relativePath, String content, Charset charset) {
    byte[] bytes = content.getBytes(charset);
    writeBytes(projectRoot, relativePath, bytes);
  }

  void createDirectories(Path projectRoot, Path relativeDir) {
    Path target = projectRoot.resolve(relativeDir);
    try {
      Files.createDirectories(target);
    } catch (IOException e) {
      throw new ProjectWriteException(target, e);
    }
  }
}
