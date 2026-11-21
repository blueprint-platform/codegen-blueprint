package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectArchiveIOException;
import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectOutputDiscoveryException;
import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemProjectPublisherTest {
  @TempDir Path target;

  private final FileSystemProjectPublisher publisher =
      new FileSystemProjectPublisher(
          new FileSystemProjectWriterAdapter(),
          new FileSystemProjectArchiverAdapter(),
          new FileSystemProjectOutputAdapter());
  private final GeneratedTextResource file =
      new GeneratedTextResource(Path.of("src/main.txt"), "test", StandardCharsets.UTF_8);

  @Test
  void preservesExistingArchiveAndCanRetryAfterTheUserRemovesIt() throws Exception {
    Path archive = Files.writeString(target.resolve("demo.zip"), "user content");
    assertThatThrownBy(this::publish).isInstanceOf(ProjectWriteException.class);
    assertThat(archive).hasContent("user content");
    try (var entries = Files.list(target)) {
      assertThat(entries.toList()).containsExactly(archive);
    }
    Files.delete(archive);
    assertThat(publish()).isTrue();
    assertThat(target.resolve("demo/src/main.txt")).hasContent("test");
  }

  @Test
  void preservesExistingProjectDirectory() throws Exception {
    Path project = Files.createDirectory(target.resolve("demo"));
    Path ownedByUser = Files.writeString(project.resolve("README.md"), "user content");

    assertThatThrownBy(this::publish).isInstanceOf(ProjectWriteException.class);
    assertThat(ownedByUser).hasContent("user content");
    assertThat(target.resolve("demo.zip")).doesNotExist();
    try (var entries = Files.list(target)) {
      assertThat(entries.toList()).containsExactly(project);
    }
  }

  @Test
  void preservesFilesAtTheProjectOrTargetPath() throws Exception {
    Path project = Files.writeString(target.resolve("demo"), "existing project path");
    Path occupiedTarget = Files.writeString(target.resolve("parent"), "existing target path");

    assertThatThrownBy(this::publish).isInstanceOf(ProjectWriteException.class);
    var resources = List.of(file);
    assertThatThrownBy(() -> publisher.publish(occupiedTarget, "demo", resources))
        .isInstanceOf(ProjectWriteException.class);
    assertThat(project).hasContent("existing project path");
    assertThat(occupiedTarget).hasContent("existing target path");
    assertThat(target.resolve("demo.zip")).doesNotExist();
  }

  @Test
  void treatsBrokenSymlinkAsAnExistingOutput() throws Exception {
    Path missing = target.resolve("missing");
    Path archive = Files.createSymbolicLink(target.resolve("demo.zip"), missing);
    assertThatThrownBy(this::publish).isInstanceOf(ProjectWriteException.class);
    assertThat(Files.isSymbolicLink(archive)).isTrue();
    assertThat(Files.readSymbolicLink(archive)).isEqualTo(missing);
    assertThat(target.resolve("demo")).doesNotExist();
    assertThat(missing).doesNotExist();
  }

  @Test
  void rejectsDuplicateOutputBeforeCreatingTheTarget() {
    Path absent = target.resolve("absent");
    var duplicateResources = List.of(file, file);
    assertThatThrownBy(() -> publisher.publish(absent, "demo", duplicateResources))
        .isInstanceOf(ProjectWriteException.class)
        .hasRootCauseMessage("Duplicate generated path: " + file.relativePath());
    assertThat(absent).doesNotExist();
  }

  @Test
  void rejectsAFileAsAParentBeforeCreatingTheTarget() {
    Path absent = target.resolve("absent");
    var parent = new GeneratedTextResource(Path.of("src"), "file", StandardCharsets.UTF_8);
    var resources = List.of(file, parent);

    assertThatThrownBy(() -> publisher.publish(absent, "demo", resources))
        .isInstanceOf(ProjectWriteException.class)
        .hasRootCauseMessage("Generated file is also a parent: src");
    assertThat(absent).doesNotExist();
  }

  @Test
  void refusesPublicationWhenProviderCannotIdentifyStaging() throws Exception {
    var uri = URI.create("jar:" + target.resolve("fixture.zip").toUri());
    try (var filesystem = FileSystems.newFileSystem(uri, Map.of("create", "true"))) {
      Path root = Files.createDirectory(filesystem.getPath("/out"));
      Path existing = Files.writeString(root.resolve("existing.txt"), "user content");
      assertThat(Files.readAttributes(root, BasicFileAttributes.class).fileKey()).isNull();
      var resources = List.of(file);

      assertThatThrownBy(() -> publisher.publish(root, "demo", resources))
          .isInstanceOf(ProjectWriteException.class)
          .rootCause()
          .hasMessageContaining("Staging directory identity changed or unavailable");
      assertThat(Files.readString(existing)).isEqualTo("user content");
      assertThat(Files.notExists(root.resolve("demo"))).isTrue();
      assertThat(Files.notExists(root.resolve("demo.zip"))).isTrue();
      try (var entries = Files.list(root)) {
        assertThat(entries.map(path -> path.getFileName().toString()).toList())
            .anyMatch(name -> name.startsWith(".codegen-"));
      }
    }
  }

  @Test
  void preservesReplacedStagingAndReportsArchiveFailure() throws IOException {
    var archiver = mock(FileSystemProjectArchiverAdapter.class);
    when(archiver.archive(any(Path.class), eq("demo")))
        .thenAnswer(
            call -> {
              Path tree = call.getArgument(0);
              Path staging = tree.getParent();
              Files.move(staging, target.resolve("original-staging"));
              Files.createDirectory(staging);
              Files.writeString(staging.resolve("user.txt"), "replacement");
              throw new ProjectArchiveIOException(tree, new IOException("archive failed"));
            });
    var tested =
        new FileSystemProjectPublisher(
            new FileSystemProjectWriterAdapter(), archiver, new FileSystemProjectOutputAdapter());
    var resources = List.of(file);

    assertThatThrownBy(() -> tested.publish(target, "demo", resources))
        .isInstanceOf(ProjectArchiveIOException.class)
        .hasRootCauseMessage("archive failed")
        .satisfies(
            failure ->
                assertThat(failure.getSuppressed())
                    .singleElement()
                    .satisfies(
                        cleanup ->
                            assertThat(cleanup)
                                .hasMessageContaining(
                                    "Staging directory identity changed or unavailable")));
    try (var paths = Files.list(target)) {
      assertThat(
              paths
                  .filter(path -> path.getFileName().toString().startsWith(".codegen-"))
                  .map(path -> path.resolve("user.txt"))
                  .toList())
          .singleElement()
          .satisfies(path -> assertThat(path).hasContent("replacement"));
    }
    assertThat(target.resolve("demo.zip")).doesNotExist();
  }

  @Test
  void rollbackLeavesReplacementSymlinkAndItsTargetAlone() throws IOException {
    var output = mock(FileSystemProjectOutputAdapter.class);
    Path elsewhere = Files.createDirectory(target.resolve("elsewhere"));
    Files.writeString(elsewhere.resolve("user.txt"), "user content");
    Path publishedRoot = target.resolve("demo");
    when(output.list(publishedRoot))
        .thenAnswer(
            call -> {
              Files.move(publishedRoot, target.resolve("moved-project"));
              Files.createSymbolicLink(publishedRoot, elsewhere);
              throw new ProjectOutputDiscoveryException(
                  publishedRoot, new IOException("listing failed"));
            });
    var tested =
        new FileSystemProjectPublisher(
            new FileSystemProjectWriterAdapter(), new FileSystemProjectArchiverAdapter(), output);
    var resources = List.of(file);

    assertThatThrownBy(() -> tested.publish(target, "demo", resources))
        .isInstanceOf(ProjectOutputDiscoveryException.class)
        .hasRootCauseMessage("listing failed")
        .satisfies(failure -> assertThat(failure.getSuppressed()).isNotEmpty());
    assertThat(Files.isSymbolicLink(publishedRoot)).isTrue();
    assertThat(elsewhere.resolve("user.txt")).hasContent("user content");
    assertThat(target.resolve("demo.zip")).doesNotExist();
  }

  @Test
  void concurrentRequestsHaveOneWinner() throws Exception {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var start = new CountDownLatch(1);
      Callable<Boolean> attempt =
          () -> {
            start.await();
            try {
              return publish();
            } catch (ProjectWriteException e) {
              return false;
            }
          };
      var first = executor.submit(attempt);
      var second = executor.submit(attempt);
      start.countDown();
      assertThat(List.of(first.get(), second.get())).containsExactlyInAnyOrder(true, false);
    }
    assertThat(target.resolve("demo/src/main.txt")).hasContent("test");
    assertThat(target.resolve("demo.zip")).isRegularFile();
  }

  private boolean publish() {
    publisher.publish(target, "demo", List.of(file));
    return true;
  }
}
