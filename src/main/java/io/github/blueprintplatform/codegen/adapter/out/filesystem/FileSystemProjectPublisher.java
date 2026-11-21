package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
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
      verifyStaging(staging, stagingKey);
      Path tree = Files.createDirectory(staging.resolve(name));
      writer.write(tree, manifest);
      Path stagedZip = archiver.archive(tree, name);
      verifyStaging(staging, stagingKey);
      publishStaged(tree, stagedZip, root, zip, claims);
      var result = new PublishedProject(root, zip, output.list(root));
      deleteStaging(staging, stagingKey);
      return result;
    } catch (IOException | RuntimeException failure) {
      claims.rollback(failure);
      if (staging != null) {
        try {
          deleteStaging(staging, stagingKey);
        } catch (IOException | RuntimeException cleanupFailure) {
          failure.addSuppressed(cleanupFailure);
        }
      }
      if (failure instanceof AdapterException adapterFailure) {
        throw adapterFailure;
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
          try (var input = Files.newInputStream(source, LinkOption.NOFOLLOW_LINKS);
              var file = claims.createFile(destination)) {
            input.transferTo(file);
          }
        }
      }
      try (var input = Files.newInputStream(stagedZip, LinkOption.NOFOLLOW_LINKS)) {
        input.transferTo(archive);
      }
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

  private static void verifyStaging(Path staging, Object stagingKey) throws IOException {
    if (!new OwnedPath(staging, stagingKey, true).matches()) {
      throw new IOException("Staging directory identity changed or unavailable: " + staging);
    }
  }

  private static void deleteStaging(Path staging, Object stagingKey) throws IOException {
    if (Files.notExists(staging, LinkOption.NOFOLLOW_LINKS)) return;
    verifyStaging(staging, stagingKey);
    try (var walk = Files.walk(staging)) {
      for (Path entry : walk.sorted(Comparator.reverseOrder()).toList()) {
        verifyStaging(staging, stagingKey);
        Files.delete(entry);
      }
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
      // Validate from the root down, before reading attributes through any nested directory.
      verifyAncestors(path.getParent());
    }

    private void verifyAncestors(Path directory) throws IOException {
      if (!root.equals(directory)) {
        verifyAncestors(directory.getParent());
      }
      verifyDirectory(directory);
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
      if (claim == null || !claim.directory() || !claim.matches()) {
        throw new IOException("Output parent identity changed: " + directory);
      }
    }
  }

  private record OwnedPath(Path path, Object key, boolean directory) {
    boolean matches() throws IOException {
      if (key == null) return false;
      var actual = attributes(path);
      return key.equals(actual.fileKey())
          && (directory ? actual.isDirectory() : actual.isRegularFile());
    }
  }
}
