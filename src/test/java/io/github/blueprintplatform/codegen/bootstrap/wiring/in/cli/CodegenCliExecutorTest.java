package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import io.github.blueprintplatform.codegen.adapter.error.exception.filesystem.ProjectWriteException;
import io.github.blueprintplatform.codegen.adapter.in.cli.CodegenCommand;
import io.github.blueprintplatform.codegen.adapter.in.cli.mapper.CreateProjectCommandMapper;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.SpringBootGenerateCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.in.project.model.ProjectSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ArchitectureSpecSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectMetadataSummary;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.ResourceBundleMessageSource;
import picocli.CommandLine;

@Tag("unit")
@Tag("bootstrap")
class CodegenCliExecutorTest {

  @TempDir Path tempDir;

  @Test
  @DisplayName("execute() should run springboot subcommand and invoke use case with mapped command")
  void execute_shouldRunSpringBootCommandAndInvokeUseCase() {
    RecordingCreateProjectPort useCase = new RecordingCreateProjectPort();

    int exitCode = getExitCode(useCase);

    assertThat(exitCode).isZero();
    assertThat(useCase.lastCreateProjectCommand).isNotNull();

    CreateProjectCommand cmd = useCase.lastCreateProjectCommand;

    assertThat(cmd.groupId()).isEqualTo("com.acme");
    assertThat(cmd.artifactId()).isEqualTo("demo-app");
    assertThat(cmd.projectName()).isEqualTo("Demo App");
    assertThat(cmd.projectDescription()).isEqualTo("Demo application for Acme");
    assertThat(cmd.packageName()).isEqualTo("com.acme.demo");

    assertThat(cmd.layout().key()).isEqualTo("hexagonal");
    assertThat(cmd.sampleCodeOptions().level().key()).isEqualTo("basic");
    assertThat(cmd.targetDirectory()).isEqualTo(tempDir);

    assertThat(cmd.dependencies()).hasSize(1);
    var dep = cmd.dependencies().getFirst();
    assertThat(dep.groupId()).isEqualTo("org.springframework.boot");
    assertThat(dep.artifactId()).isEqualTo("spring-boot-starter-webmvc");
  }

  @Test
  void localizesOutputIntegrityAndCleanupDetails() {
    var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    var handler = new CodegenCliExceptionHandler(messages);
    var command = new CommandLine(new CodegenCommand());
    var err = new StringWriter();
    command.setErr(new PrintWriter(err, true));

    var duplicate = new AdapterException("adapter.project.output.duplicate", "src/main.txt") {};
    duplicate.addSuppressed(
        new AdapterException("adapter.project.staging.identity.changed", "staging") {});
    var failure = new ProjectWriteException(Path.of("demo"), duplicate);

    assertThat(handler.handleExecutionException(failure, command, null)).isEqualTo(3);
    assertThat(err.toString())
        .contains("Could not write project output at demo: Duplicate generated path: src/main.txt")
        .contains("(code: adapter.project.write.failed)")
        .contains("codegen: cleanup: Staging directory identity changed or unavailable: staging");
  }

  private int getExitCode(RecordingCreateProjectPort useCase) {
    CreateProjectCommandMapper mapper = new CreateProjectCommandMapper();
    SpringBootGenerateCommand springBootCmd = new SpringBootGenerateCommand(mapper, useCase);

    CodegenCommand rootCommand = new CodegenCommand();
    CommandLine.IFactory factory = new TestPicocliFactory(springBootCmd);

    CodegenCliExceptionHandler handler = new CodegenCliExceptionHandler(new DummyMessageSource());
    CodegenCliExecutor executor = new CodegenCliExecutor(rootCommand, factory, handler);

    String[] args = {
      "springboot",
      "--group-id",
      "com.acme",
      "--artifact-id",
      "demo-app",
      "--name",
      "Demo App",
      "--description",
      "Demo application for Acme",
      "--package-name",
      "com.acme.demo",
      "--layout",
      "hexagonal",
      "--sample-code",
      "basic",
      "--dependency",
      "web",
      "--target-dir",
      tempDir.toString()
    };

    return executor.execute(args);
  }

  static class RecordingCreateProjectPort implements CreateProjectPort {

    CreateProjectCommand lastCreateProjectCommand;

    @Override
    public CreateProjectResult handle(CreateProjectCommand createProjectCommand) {
      this.lastCreateProjectCommand = createProjectCommand;

      var metadata =
          new ProjectMetadataSummary(
              lastCreateProjectCommand.groupId(),
              lastCreateProjectCommand.artifactId(),
              lastCreateProjectCommand.projectName(),
              lastCreateProjectCommand.projectDescription(),
              lastCreateProjectCommand.packageName());

      var target = new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0");
      var platform = new PlatformSpec(createProjectCommand.techStack(), target);

      var architecture =
          new ArchitectureSpecSummary(
              lastCreateProjectCommand.layout().key(),
              lastCreateProjectCommand.guardrailsMode().key(),
              lastCreateProjectCommand.sampleCodeOptions().level().key());

      var project =
          new ProjectSummary(
              metadata, platform, architecture, List.of(), List.of());

      return new CreateProjectResult(
          project,
          lastCreateProjectCommand.targetDirectory(),
          Path.of("demo-app.zip"),
          new TargetResolution(target, TargetResolution.Source.REMOTE_LATEST, null));
    }
  }

  static class TestPicocliFactory implements CommandLine.IFactory {

    private final SpringBootGenerateCommand springBootCommand;
    private final CommandLine.IFactory delegate = CommandLine.defaultFactory();

    TestPicocliFactory(SpringBootGenerateCommand springBootCommand) {
      this.springBootCommand = springBootCommand;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> K create(Class<K> cls) throws Exception {
      if (cls == SpringBootGenerateCommand.class) {
        return (K) springBootCommand;
      }
      return delegate.create(cls);
    }
  }

  static class DummyMessageSource implements MessageSource {

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
      return (defaultMessage != null) ? defaultMessage : code;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) {
      return code;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
      return resolvable.getDefaultMessage() != null
          ? resolvable.getDefaultMessage()
          : (resolvable.getCodes() != null && resolvable.getCodes().length > 0
              ? resolvable.getCodes()[0]
              : "unknown");
    }
  }
}
