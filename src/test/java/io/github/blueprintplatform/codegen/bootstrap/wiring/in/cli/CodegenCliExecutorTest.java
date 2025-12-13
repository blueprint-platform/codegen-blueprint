package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.in.cli.CodegenCliExceptionHandler;
import io.github.blueprintplatform.codegen.adapter.in.cli.CodegenCommand;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.CreateProjectCommandMapper;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.SpringBootGenerateCommand;
import io.github.blueprintplatform.codegen.application.usecase.project.CreateProjectUseCase;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.usecase.project.model.ProjectGenerationSummary;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import picocli.CommandLine;

@Tag("unit")
@Tag("bootstrap")
class CodegenCliExecutorTest {

  @TempDir Path tempDir;

  @Test
  @DisplayName("execute() should run springboot subcommand and invoke use case with mapped command")
  void execute_shouldRunSpringBootCommandAndInvokeUseCase() {
    RecordingCreateProjectUseCase useCase = new RecordingCreateProjectUseCase();
    int exitCode = getExitCode(useCase);

    assertThat(exitCode).isZero();
    assertThat(useCase.lastCommand).isNotNull();

    CreateProjectCommand cmd = useCase.lastCommand;

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
    assertThat(dep.artifactId()).isEqualTo("spring-boot-starter-web");
  }

  private int getExitCode(RecordingCreateProjectUseCase useCase) {
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

  static class RecordingCreateProjectUseCase implements CreateProjectUseCase {
    CreateProjectCommand lastCommand;

    @Override
    public CreateProjectResult handle(CreateProjectCommand command) {
      this.lastCommand = command;

      var project =
          new ProjectGenerationSummary(
              command.groupId(),
              command.artifactId(),
              command.projectName(),
              command.projectDescription(),
              command.packageName(),
              new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA),
              command.layout(),
              new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V3_5),
              SampleCodeOptions.none(),
              List.of());

      return new CreateProjectResult(
          project, command.targetDirectory(), Path.of("demo-app.zip"), List.of());
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
