package io.github.bsayli.codegen.initializr.domain.model;

import io.github.bsayli.codegen.initializr.domain.model.value.dependency.Dependencies;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.ProjectIdentity;
import io.github.bsayli.codegen.initializr.domain.model.value.naming.ProjectDescription;
import io.github.bsayli.codegen.initializr.domain.model.value.naming.ProjectName;
import io.github.bsayli.codegen.initializr.domain.model.value.pkg.PackageName;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.PlatformTarget;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;

public class ProjectBlueprint {

  private final ProjectIdentity identity;
  private final ProjectName name;
  private final ProjectDescription description;
  private final PackageName packageName;
  private final BuildOptions buildOptions;
  private final PlatformTarget platformTarget;
  private final Dependencies dependencies;

  public ProjectBlueprint(
      ProjectIdentity identity,
      ProjectName name,
      ProjectDescription description,
      PackageName packageName,
      BuildOptions buildOptions,
      PlatformTarget platformTarget,
      Dependencies dependencies) {
    this.identity = identity;
    this.name = name;
    this.description = description;
    this.packageName = packageName;
    this.buildOptions = buildOptions;
    this.platformTarget = platformTarget;
    this.dependencies = dependencies;
  }

  public ProjectIdentity getIdentity() {
    return identity;
  }

  public ProjectName getName() {
    return name;
  }

  public ProjectDescription getDescription() {
    return description;
  }

  public PackageName getPackageName() {
    return packageName;
  }

  public BuildOptions getBuildOptions() {
    return buildOptions;
  }

  public PlatformTarget getPlatformTarget() {
    return platformTarget;
  }

  public Dependencies getDependencies() {
    return dependencies;
  }
}
