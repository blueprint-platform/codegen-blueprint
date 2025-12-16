package io.github.blueprintplatform.codegen.application.usecase.project.mapper;

import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.ProjectSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.ArchitectureSpecSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.ProjectDependencySummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.ProjectFileSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.ProjectMetadataSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.RuntimeTargetSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.TechStackSummary;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateProjectResponseMapper {

  private static final String RUNTIME_TARGET_SPRING_BOOT_JVM = "spring-boot-jvm";
  private static final String PARAM_JAVA_VERSION = "javaVersion";
  private static final String PARAM_SPRING_BOOT_VERSION = "springBootVersion";

  public CreateProjectResult from(
      ProjectBlueprint blueprint,
      Path projectRoot,
      List<ProjectOutputItem> projectOutputItems,
      Path archivePath) {

    ProjectSummary project = toProjectSummary(blueprint, projectOutputItems);
    return new CreateProjectResult(project, projectRoot, archivePath);
  }

  private ProjectSummary toProjectSummary(
      ProjectBlueprint blueprint, List<ProjectOutputItem> projectOutputItems) {
    ProjectMetadata metadata = blueprint.getMetadata();
    PlatformSpec platform = blueprint.getPlatform();
    ArchitectureSpec architecture = blueprint.getArchitecture();
    Dependencies dependencies = blueprint.getDependencies();

    ProjectMetadataSummary metadataSummary = toMetadataSummary(metadata);
    TechStackSummary techStackSummary = toTechStackSummary(platform);
    RuntimeTargetSummary runtimeTargetSummary = toRuntimeTargetSummary(platform);
    ArchitectureSpecSummary architectureSummary = toArchitectureSummary(architecture);

    List<ProjectDependencySummary> dependencySummaries =
        dependencies.asList().stream().map(this::toProjectDependencySummary).toList();

    List<ProjectFileSummary> files =
        projectOutputItems.stream().map(this::toProjectFileSummary).toList();

    return new ProjectSummary(
        metadataSummary,
        techStackSummary,
        runtimeTargetSummary,
        architectureSummary,
        dependencySummaries,
        files);
  }

  private ProjectMetadataSummary toMetadataSummary(ProjectMetadata metadata) {
    return new ProjectMetadataSummary(
        metadata.identity().groupId().value(),
        metadata.identity().artifactId().value(),
        metadata.name().value(),
        metadata.description().value(),
        metadata.packageName().value());
  }

  private ArchitectureSpecSummary toArchitectureSummary(ArchitectureSpec architecture) {
    String layout = architecture.layout().key();
    String enforcementMode = architecture.governance().mode().key();
    String sampleCode = architecture.sampleCodeOptions().level().key();

    return new ArchitectureSpecSummary(layout, enforcementMode, sampleCode);
  }

  private TechStackSummary toTechStackSummary(PlatformSpec platform) {
    var stack = platform.techStack();

    String framework = stack.framework().key();
    String buildTool = stack.buildTool().key();
    String language = stack.language().key();

    String frameworkVersion = null;
    String languageVersion = null;

    if (platform.platformTarget()
        instanceof SpringBootJvmTarget(JavaVersion java, SpringBootVersion springBoot)) {
      frameworkVersion = springBoot.defaultVersion();
      languageVersion = java.asString();
    }

    return new TechStackSummary(
        framework, frameworkVersion, buildTool, null, language, languageVersion);
  }

  private RuntimeTargetSummary toRuntimeTargetSummary(PlatformSpec platform) {
    return switch (platform.platformTarget()) {
      case SpringBootJvmTarget(JavaVersion java, SpringBootVersion springBoot) ->
          new RuntimeTargetSummary(
              RUNTIME_TARGET_SPRING_BOOT_JVM,
              Map.of(
                  PARAM_JAVA_VERSION, java.asString(),
                  PARAM_SPRING_BOOT_VERSION, springBoot.defaultVersion()));
    };
  }

  private ProjectFileSummary toProjectFileSummary(ProjectOutputItem item) {
    return new ProjectFileSummary(item.relativePath(), item.binary(), item.executable());
  }

  private ProjectDependencySummary toProjectDependencySummary(Dependency dependency) {
    DependencyCoordinates coordinates = dependency.coordinates();
    String version = dependency.version() != null ? dependency.version().value() : null;
    String scope =
        dependency.scope() != null ? dependency.scope().name().toLowerCase(Locale.ROOT) : null;

    return new ProjectDependencySummary(
        coordinates.groupId().value(), coordinates.artifactId().value(), version, scope);
  }
}
