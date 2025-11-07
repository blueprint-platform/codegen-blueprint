package io.github.bsayli.codegen.initializr.adapter.out.build.maven.shared;

public final class PomDependency {
  private final String groupId;
  private final String artifactId;
  private final String version; // nullable
  private final String scope; // nullable

  private PomDependency(String groupId, String artifactId, String version, String scope) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.scope = scope;
  }

  public static PomDependency of(String groupId, String artifactId) {
    return new PomDependency(groupId, artifactId, null, null);
  }

  public static PomDependency of(String groupId, String artifactId, String version, String scope) {
    return new PomDependency(groupId, artifactId, version, scope);
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getScope() {
    return scope;
  }
}
