package io.github.blueprintplatform.codegen.domain.model;

import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.policy.tech.CompatibilityPolicy;

public final class ProjectBlueprint {

  private final ProjectMetadata metadata;
  private final PlatformSpec platform;
  private final ArchitectureSpec architecture;
  private final Dependencies dependencies;

  private ProjectBlueprint(
      ProjectMetadata metadata,
      PlatformSpec platform,
      ArchitectureSpec architecture,
      Dependencies dependencies) {
    this.metadata = metadata;
    this.platform = platform;
    this.architecture = architecture;
    this.dependencies = dependencies;
  }

  public static ProjectBlueprint of(
      ProjectMetadata metadata,
      PlatformSpec platform,
      ArchitectureSpec architecture,
      Dependencies dependencies) {

    CompatibilityPolicy.ensureCompatible(platform.techStack(), platform.platformTarget());

    return new ProjectBlueprint(metadata, platform, architecture, dependencies);
  }

  public ProjectMetadata getMetadata() {
    return metadata;
  }

  public PlatformSpec getPlatform() {
    return platform;
  }

  public ArchitectureSpec getArchitecture() {
    return architecture;
  }

  public Dependencies getDependencies() {
    return dependencies;
  }
}
