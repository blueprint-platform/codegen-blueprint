package com.c9.codegen.initializr.projectgeneration.model;

import java.util.List;

public class ProjectMetadata {

  private String name;
  private String description;
  private String groupId;
  private String artifactId;
  private String packageName;
  private List<Dependency> dependencies;

  protected ProjectMetadata(ProjectMetadataBuilder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.groupId = builder.groupId;
    this.artifactId = builder.artifactId;
    this.packageName = builder.packageName;
    this.dependencies = builder.dependencies;
  }

  public String getName() {
    return name;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getDescription() {
    return description;
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public static class ProjectMetadataBuilder {

    private String name;
    private String description;
    private String groupId;
    private String artifactId;
    private String packageName;
    private List<Dependency> dependencies;

    public ProjectMetadataBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProjectMetadataBuilder description(String description) {
      this.description = description;
      return this;
    }

    public ProjectMetadataBuilder groupId(String groupId) {
      this.groupId = groupId;
      return this;
    }

    public ProjectMetadataBuilder artifactId(String artifactId) {
      this.artifactId = artifactId;
      return this;
    }

    public ProjectMetadataBuilder packageName(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public ProjectMetadataBuilder dependencies(List<Dependency> dependencies) {
      this.dependencies = dependencies;
      return this;
    }

    public ProjectMetadata build() {
      return new ProjectMetadata(this);
    }
  }
}
