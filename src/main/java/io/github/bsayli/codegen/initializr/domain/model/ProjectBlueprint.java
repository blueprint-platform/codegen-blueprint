package io.github.bsayli.codegen.initializr.domain.model;

import io.github.bsayli.codegen.initializr.domain.model.value.*;

public class ProjectBlueprint {

  private final ProjectName name;
  private final ProjectDescription description;
  private final ProjectIdentity projectIdentity;
  private final PackageName packageName;

  public ProjectBlueprint(
      ProjectName name,
      ProjectDescription description,
      ProjectIdentity projectIdentity,
      PackageName packageName) {
    this.name = name;
    this.description = description;
    this.projectIdentity = projectIdentity;
    this.packageName = packageName;
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

  public ProjectIdentity getProjectIdentity() {
    return projectIdentity;
  }
}
