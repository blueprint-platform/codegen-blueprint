package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FileSystemProjectPublisher implements ProjectPublisherPort {
  private final FileSystemProjectWriterAdapter writer = new FileSystemProjectWriterAdapter();
  private final FileSystemProjectArchiverAdapter archiver = new FileSystemProjectArchiverAdapter();
  private final FileSystemProjectOutputAdapter output = new FileSystemProjectOutputAdapter();

  @Override
  public PublishedProject publish(
      Path target, String artifactId, Iterable<? extends GeneratedResource> resources) {
    String name = new ArtifactId(artifactId).value();
    List<GeneratedResource> manifest = validatedManifest(resources);
    Path root = target.resolve(name);
    Path zip = target.resolve(name + ".zip");
    List<OwnedPath> claimed = new ArrayList<>();
    Path staging = null;
    Object stagingKey = null;
    try {
      Files.createDirectories(target);
      staging = Files.createTempDirectory(target, ".codegen-");
      stagingKey = attributes(staging).fileKey();
      Path tree = Files.createDirectory(staging.resolve(name));
      writer.write(tree, manifest);
      Path stagedZip = archiver.archive(tree, name);
      publishStaged(tree, stagedZip, root, zip, claimed);
      var result = new PublishedProject(root, zip, output.list(root));
      deleteStaging(staging, stagingKey);
      return result;
    } catch (IOException | RuntimeException failure) {
      rollbackOutput(claimed, root, failure);
      if (staging != null) {
        try {
          deleteStaging(staging, stagingKey);
        } catch (IOException cleanupFailure) {
          failure.addSuppressed(cleanupFailure);
        }
      }
      throw new ProjectWriteException(root, failure);
    }
  }

  private static void publishStaged(
      Path tree, Path stagedZip, Path root, Path zip, List<OwnedPath> claimed) throws IOException {
    List<Path> entries;
    try (var walk = Files.walk(tree)) {
      entries = walk.filter(path -> !path.equals(tree)).sorted().toList();
    }
    Files.createDirectory(root);
    remember(claimed, root, true);
    try (var archive =
        Files.newOutputStream(zip, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
      remember(claimed, zip, false);
      for (Path source : entries) {
        Path destination = root.resolve(tree.relativize(source));
        verifyDirectory(claimed, root);
        verifyDirectory(claimed, destination.getParent());
        copyEntry(source, destination, claimed);
      }
      Files.copy(stagedZip, archive);
    }
  }

  private static void copyEntry(Path source, Path destination, List<OwnedPath> claimed)
      throws IOException {
    if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
      Files.createDirectory(destination);
      remember(claimed, destination, true);
    } else {
      try (var file =
          Files.newOutputStream(
              destination, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
        remember(claimed, destination, false);
        Files.copy(source, file);
      }
    }
  }

  private static void rollbackOutput(List<OwnedPath> claimed, Path root, Throwable failure) {
    for (OwnedPath entry : claimed.reversed()) {
      try {
        if (!entry.path().equals(root) && entry.path().startsWith(root)) {
          verifyDirectory(claimed, root);
          verifyDirectory(claimed, entry.path().getParent());
        }
        if (!entry.matches())
          throw new IOException("Output identity changed or unavailable: " + entry.path());
        Files.delete(entry.path());
      } catch (IOException cleanupFailure) {
        failure.addSuppressed(cleanupFailure);
      }
    }
  }

  private static List<GeneratedResource> validatedManifest(
      Iterable<? extends GeneratedResource> resources) {
    Map<Path, GeneratedResource> entries = new LinkedHashMap<>();
    for (GeneratedResource resource : resources) {
      Path path = resource.relativePath();
      GeneratedResource previous = entries.putIfAbsent(path, resource);
      if (previous != null
          && !(previous instanceof GeneratedDirectory && resource instanceof GeneratedDirectory)) {
        throw new IllegalArgumentException("Duplicate generated path: " + path);
      }
    }
    for (Path path : entries.keySet()) {
      for (Path parent = path.getParent(); parent != null; parent = parent.getParent()) {
        if (entries.containsKey(parent) && !(entries.get(parent) instanceof GeneratedDirectory)) {
          throw new IllegalArgumentException("Generated file is also a parent: " + parent);
        }
      }
    }
    return List.copyOf(entries.values());
  }

  private static void remember(List<OwnedPath> paths, Path path, boolean directory)
      throws IOException {
    paths.add(new OwnedPath(path, attributes(path).fileKey(), directory));
  }

  private static void verifyDirectory(List<OwnedPath> paths, Path directory) throws IOException {
    for (OwnedPath path : paths.reversed()) {
      if (path.path().equals(directory) && path.directory()) {
        if (path.key() == null
            ? Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)
            : path.matches()) {
          return;
        }
        break;
      }
    }
    throw new IOException("Output parent identity changed: " + directory);
  }

  private static BasicFileAttributes attributes(Path path) throws IOException {
    return Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
  }

  private static void deleteStaging(Path staging, Object stagingKey) throws IOException {
    if (!Files.exists(staging, LinkOption.NOFOLLOW_LINKS)) return;
    var current = attributes(staging);
    if (!current.isDirectory()
        || stagingKey != null && !Objects.equals(stagingKey, current.fileKey())) {
      throw new IOException("Staging directory identity changed: " + staging);
    }
    try (var walk = Files.walk(staging)) {
      for (Path entry : walk.sorted(Comparator.reverseOrder()).toList()) Files.delete(entry);
    }
  }

  private record OwnedPath(Path path, Object key, boolean directory) {
    boolean matches() throws IOException {
      var actual = attributes(path);
      return key != null
          && Objects.equals(key, actual.fileKey())
          && (directory ? actual.isDirectory() : actual.isRegularFile());
    }
  }
}
