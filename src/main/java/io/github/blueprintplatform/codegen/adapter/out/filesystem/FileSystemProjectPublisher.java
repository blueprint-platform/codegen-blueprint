package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;

public final class FileSystemProjectPublisher implements ProjectPublisherPort {
  private final FileSystemProjectWriterAdapter writer;
  private final FileSystemProjectArchiverAdapter archiver;
  private final FileSystemProjectOutputAdapter output;

  public FileSystemProjectPublisher(
      FileSystemProjectWriterAdapter writer,
      FileSystemProjectArchiverAdapter archiver,
      FileSystemProjectOutputAdapter output) {
    this.writer = writer;
    this.archiver = archiver;
    this.output = output;
  }

  @Override
  public PublishedProject publish(
      Path target, String artifactId, Iterable<? extends GeneratedResource> resources) {
    String name = new ArtifactId(artifactId).value();
    Path root = target.resolve(name);
    Path zip = target.resolve(name + ".zip");
    var claims = new OutputClaims(root);
    Path staging = null;
    Object stagingKey = null;
    try {
      var manifest = validatedManifest(resources);
      Files.createDirectories(target);
      staging = Files.createTempDirectory(target, ".codegen-");
      stagingKey = attributes(staging).fileKey();
      Path tree = Files.createDirectory(staging.resolve(name));
      writer.write(tree, manifest);
      Path stagedZip = archiver.archive(tree, name);
      publishStaged(tree, stagedZip, root, zip, claims);
      var result = new PublishedProject(root, zip, output.list(root));
      deleteStaging(staging, stagingKey);
      return result;
    } catch (IOException | RuntimeException failure) {
      claims.rollback(failure);
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
      Path tree, Path stagedZip, Path root, Path zip, OutputClaims claims) throws IOException {
    List<Path> entries;
    try (var walk = Files.walk(tree)) {
      entries = walk.filter(path -> !path.equals(tree)).sorted().toList();
    }
    claims.createDirectory(root);
    try (var archive = claims.createFile(zip)) {
      for (Path source : entries) {
        Path destination = root.resolve(tree.relativize(source));
        claims.verifyParents(destination);
        if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
          claims.createDirectory(destination);
        } else {
          try (var file = claims.createFile(destination)) {
            Files.copy(source, file);
          }
        }
      }
      Files.copy(stagedZip, archive);
    }
  }

  private static List<GeneratedResource> validatedManifest(
      Iterable<? extends GeneratedResource> resources) throws IOException {
    Map<Path, GeneratedResource> entries = new LinkedHashMap<>();
    for (GeneratedResource resource : resources) {
      Path path = resource.relativePath();
      GeneratedResource previous = entries.putIfAbsent(path, resource);
      if (previous != null
          && !(previous instanceof GeneratedDirectory && resource instanceof GeneratedDirectory)) {
        throw new IOException("Duplicate generated path: " + path);
      }
    }
    for (Path path : entries.keySet()) {
      for (Path parent = path.getParent(); parent != null; parent = parent.getParent()) {
        if (entries.containsKey(parent) && !(entries.get(parent) instanceof GeneratedDirectory)) {
          throw new IOException("Generated file is also a parent: " + parent);
        }
      }
    }
    return List.copyOf(entries.values());
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

  /** Owns the paths claimed by a single publication, in creation order. */
  private static final class OutputClaims {
    private final Path root;
    private final SequencedMap<Path, OwnedPath> paths = new LinkedHashMap<>();

    OutputClaims(Path root) {
      this.root = root;
    }

    void createDirectory(Path path) throws IOException {
      Files.createDirectory(path);
      remember(path, true);
    }

    OutputStream createFile(Path path) throws IOException {
      var stream =
          Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
      try {
        remember(path, false);
        return stream;
      } catch (IOException | RuntimeException e) {
        try {
          stream.close();
        } catch (IOException closeFailure) {
          e.addSuppressed(closeFailure);
        }
        throw e;
      }
    }

    void verifyParents(Path path) throws IOException {
      verifyDirectory(root);
      if (!root.equals(path.getParent())) {
        verifyDirectory(path.getParent());
      }
    }

    void rollback(Throwable failure) {
      for (var claim : paths.reversed().values()) {
        try {
          if (!claim.path().equals(root) && claim.path().startsWith(root)) {
            verifyParents(claim.path());
          }
          if (!claim.matches()) {
            throw new IOException("Output identity changed or unavailable: " + claim.path());
          }
          Files.delete(claim.path());
        } catch (IOException | RuntimeException cleanupFailure) {
          failure.addSuppressed(cleanupFailure);
        }
      }
    }

    private void remember(Path path, boolean directory) throws IOException {
      paths.put(path, new OwnedPath(path, attributes(path).fileKey(), directory));
    }

    private void verifyDirectory(Path directory) throws IOException {
      var claim = paths.get(directory);
      if (claim == null
          || !claim.directory()
          || !(claim.key() == null
              ? Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)
              : claim.matches())) {
        throw new IOException("Output parent identity changed: " + directory);
      }
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
