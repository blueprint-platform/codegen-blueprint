package io.github.bsayli.codegen.initializr.application.usecase.createproject;

import static io.github.bsayli.codegen.initializr.domain.port.out.ProjectFileSystemPort.OnExistsPolicy.FAIL_IF_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.JavaVersion;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.SpringBootVersion;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Language;
import io.github.bsayli.codegen.initializr.domain.port.out.ProjectFileSystemPort;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("application")
@DisplayName("CreateProjectService")
class CreateProjectServiceTest {

  @Test
  @DisplayName("execute() prepares project root and returns blueprint")
  void creates_project_root_and_returns_blueprint() {
    var mapper = new ProjectBlueprintMapper();
    var fs = new FakeFs();
    var service = new CreateProjectService(mapper, fs);

    var cmd =
        new CreateProjectCommand(
            "com.acme",
            "demo-app",
            "Demo App",
            "desc",
            "com.acme.demo",
            new BuildOptions(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA),
            JavaVersion.JAVA_21,
            SpringBootVersion.V3_5_6,
            List.of(),
            Path.of(System.getProperty("java.io.tmpdir")));

    var result = service.execute(cmd);

    ProjectBlueprint bp = result.blueprint();
    assertThat(bp.getIdentity().groupId().value()).isEqualTo("com.acme");
    assertThat(bp.getIdentity().artifactId().value()).isEqualTo("demo-app");
    assertThat(result.projectRoot().getFileName()).hasToString("demo-app");

    assertThat(fs.lastPreparedRoot).isEqualTo(result.projectRoot());
    assertThat(fs.lastPolicy).isEqualTo(FAIL_IF_EXISTS);
  }

  static class FakeFs implements ProjectFileSystemPort {
    Path lastPreparedRoot;
    OnExistsPolicy lastPolicy;

    @Override
    public Path prepareProjectRoot(Path targetDirectory, String artifactId, OnExistsPolicy policy) {
      this.lastPolicy = policy;
      this.lastPreparedRoot = targetDirectory.resolve(artifactId);
      return lastPreparedRoot;
    }

    @Override
    public void writeBytes(Path root, Path relative, byte[] content) {
      throw new UnsupportedOperationException("FakeFs.writeBytes not expected in this test");
    }

    @Override
    public void writeText(
        Path root, Path relative, CharSequence content, java.nio.charset.Charset cs) {
      throw new UnsupportedOperationException("FakeFs.writeText not expected in this test");
    }
  }
}
