package io.github.blueprintplatform.codegen.adapter.out.filesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemProjectPublisherTest {
  @TempDir Path target;

  private final FileSystemProjectPublisher publisher = new FileSystemProjectPublisher();
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
