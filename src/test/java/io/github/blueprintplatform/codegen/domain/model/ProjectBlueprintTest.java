package io.github.blueprintplatform.codegen.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureGovernance;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.GuardrailsMode;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeLevel;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("domain")
class ProjectBlueprintTest {

  private static ProjectMetadata metadata() {
    return new ProjectMetadata(
        new ProjectIdentity(new GroupId("com.example"), new ArtifactId("demo-app")),
        new ProjectName("demo-app"),
        new ProjectDescription("simple demo project"),
        new PackageName("com.example.demo"));
  }

  private static TechStack techStack() {
    return new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);
  }

  private static PlatformTarget target() {
    return new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0");
  }

  private static PlatformSpec platform() {
    return new PlatformSpec(techStack(), target());
  }

  private static ArchitectureSpec architecture() {
    return new ArchitectureSpec(
        ProjectLayout.STANDARD,
        new ArchitectureGovernance(GuardrailsMode.NONE),
        new SampleCodeOptions(SampleCodeLevel.NONE));
  }

  private static Dependencies dependencies() {
    return Dependencies.of(List.of(dep("org.acme", "alpha")));
  }

  private static Dependency dep(String groupId, String artifactId) {
    return new Dependency(
        new DependencyCoordinates(new GroupId(groupId), new ArtifactId(artifactId)), null, null);
  }

  @Test
  @DisplayName(
      "of(metadata, platform, architecture, dependencies) should create blueprint with same"
          + " references")
  void of_shouldCreateBlueprint() {
    ProjectMetadata metadata = metadata();
    PlatformSpec platform = platform();
    ArchitectureSpec architecture = architecture();
    Dependencies dependencies = dependencies();

    ProjectBlueprint bp = ProjectBlueprint.of(metadata, platform, architecture, dependencies);

    assertThat(bp.getMetadata()).isSameAs(metadata);
    assertThat(bp.getPlatform()).isSameAs(platform);
    assertThat(bp.getArchitecture()).isSameAs(architecture);
    assertThat(bp.getDependencies()).isSameAs(dependencies);
  }

  @Test
  @DisplayName("null tech stack should fail with platform.target.missing")
  void of_nullTechStack_shouldFailPlatformTargetMissing() {
    PlatformSpec platform = new PlatformSpec(null, target());

    assertThatThrownBy(
            () -> ProjectBlueprint.of(metadata(), platform, architecture(), dependencies()))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));
  }

  @Test
  @DisplayName("null platform target should fail with platform.target.missing")
  void of_nullPlatformTarget_shouldFailPlatformTargetMissing() {
    PlatformSpec platform = new PlatformSpec(techStack(), null);

    assertThatThrownBy(
            () -> ProjectBlueprint.of(metadata(), platform, architecture(), dependencies()))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));
  }

  @Test
  void acceptsJava25WithBoot41() {
    var platform =
        new PlatformSpec(
            techStack(),
            new SpringBootJvmTarget(JavaVersion.JAVA_25, SpringBootVersion.V4_1, "4.1.3"));
    assertThat(
            ProjectBlueprint.of(metadata(), platform, architecture(), dependencies()).getPlatform())
        .isEqualTo(platform);
  }
}
