package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.ProjectFileListingException;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectFileListingPort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemProjectFileListingAdapter implements ProjectFileListingPort {

  @Override
  public List<Path> listFiles(Path projectRoot) {
    if (projectRoot == null) {
      return List.of();
    }

    Path normalizedRoot = projectRoot.toAbsolutePath().normalize();

    if (!Files.exists(normalizedRoot) || !Files.isDirectory(normalizedRoot)) {
      return List.of();
    }

    try (Stream<Path> paths = Files.walk(normalizedRoot)) {
      return paths
          .filter(Files::isRegularFile)
          .map(normalizedRoot::relativize) // her zaman relative path
          .toList();
    } catch (IOException e) {
      throw new ProjectFileListingException(normalizedRoot, e);
    }
  }
}
