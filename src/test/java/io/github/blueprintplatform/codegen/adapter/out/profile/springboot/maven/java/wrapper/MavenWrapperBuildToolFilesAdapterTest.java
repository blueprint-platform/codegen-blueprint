package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.wrapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.TemplateSpec;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureGovernance;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import io.github.blueprintplatform.codegen.testsupport.templating.NoopTemplateRenderer;
import io.github.blueprintplatform.codegen.testsupport.templating.RecordingTemplateRenderer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("adapter")
class MavenWrapperBuildToolFilesAdapterTest {

  private static final String BASE_PATH = "springboot/maven/java/";

  private static ProjectBlueprint blueprint() {
    ProjectMetadata metadata =
        new ProjectMetadata(
            new ProjectIdentity(
                new GroupId("io.github.blueprintplatform"), new ArtifactId("greeting")),
            new ProjectName("Greeting"),
            new ProjectDescription("Greeting sample service"),
            new PackageName("io.github.blueprintplatform.greeting"));

    PlatformSpec platform =
        new PlatformSpec(
            new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA),
            new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0"));

    ArchitectureSpec architecture =
        new ArchitectureSpec(
            ProjectLayout.STANDARD, ArchitectureGovernance.none(), SampleCodeOptions.none());

    return ProjectBlueprint.of(metadata, platform, architecture, Dependencies.of(List.of()));
  }

  @Test
  @DisplayName("artifactKey() should return BUILD_TOOL_FILES")
  void artifactKey_shouldReturnBuildToolFiles() {
    MavenWrapperBuildToolFilesAdapter adapter =
        new MavenWrapperBuildToolFilesAdapter(
            new NoopTemplateRenderer(),
            new ArtifactSpec(
                BASE_PATH,
                List.of(
                    new TemplateSpec(
                        "maven-wrapper.properties.ftl", ".mvn/wrapper/maven-wrapper.properties"),
                    new TemplateSpec("mvnw.ftl", "mvnw"),
                    new TemplateSpec("mvnw.cmd.ftl", "mvnw.cmd"))));

    assertThat(adapter.artifactKey()).isEqualTo(ArtifactKey.BUILD_TOOL_FILES);
  }

  @Test
  @DisplayName("generate() should render all Maven wrapper build tool files")
  void generate_shouldRenderAllBuildToolFiles() {
    GeneratedResource wrapperProps =
        new GeneratedTextResource(
            Path.of(".mvn/wrapper/maven-wrapper.properties"), "", StandardCharsets.UTF_8);

    GeneratedResource mvnw = new GeneratedTextResource(Path.of("mvnw"), "", StandardCharsets.UTF_8);

    GeneratedResource mvnwCmd =
        new GeneratedTextResource(Path.of("mvnw.cmd"), "", StandardCharsets.UTF_8);

    RecordingTemplateRenderer renderer =
        new RecordingTemplateRenderer(List.of(wrapperProps, mvnw, mvnwCmd));

    ArtifactSpec artifactSpec =
        new ArtifactSpec(
            BASE_PATH,
            List.of(
                new TemplateSpec(
                    "maven-wrapper.properties.ftl", ".mvn/wrapper/maven-wrapper.properties"),
                new TemplateSpec("mvnw.ftl", "mvnw"),
                new TemplateSpec("mvnw.cmd.ftl", "mvnw.cmd")));

    MavenWrapperBuildToolFilesAdapter adapter =
        new MavenWrapperBuildToolFilesAdapter(renderer, artifactSpec);

    Iterable<? extends GeneratedResource> result = adapter.generate(blueprint());

    assertThat(result)
        .extracting(GeneratedResource::relativePath)
        .containsExactly(
            Path.of(".mvn/wrapper/maven-wrapper.properties"), Path.of("mvnw"), Path.of("mvnw.cmd"));

    assertThat(renderer.capturedOutPaths)
        .containsExactly(
            Path.of(".mvn/wrapper/maven-wrapper.properties"), Path.of("mvnw"), Path.of("mvnw.cmd"));

    assertThat(renderer.capturedTemplateNames)
        .containsExactly(
            "springboot/maven/java/maven-wrapper.properties.ftl",
            "springboot/maven/java/mvnw.ftl",
            "springboot/maven/java/mvnw.cmd.ftl");

    assertThat(renderer.capturedModels).allSatisfy(model -> assertThat(model).isEmpty());
  }
}
