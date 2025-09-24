package io.github.bsayli.codegen.initializr.domain.model.value.dependency;

import io.github.bsayli.codegen.initializr.domain.model.value.identity.ArtifactId;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.GroupId;

public final class DependencyFactory {

  private DependencyFactory() {}

  public static DependencyCoordinates coordinates(GroupId groupId, ArtifactId artifactId) {
    return new DependencyCoordinates(groupId, artifactId);
  }

  public static Dependency of(DependencyCoordinates coordinates) {
    return new Dependency(coordinates, null, null);
  }

  public static Dependency of(DependencyCoordinates coordinates, DependencyVersion version) {
    return new Dependency(coordinates, version, null);
  }

  public static Dependency of(
      DependencyCoordinates coordinates, DependencyVersion version, DependencyScope scope) {
    return new Dependency(coordinates, version, scope);
  }

  public static Dependency of(GroupId groupId, ArtifactId artifactId) {
    return of(coordinates(groupId, artifactId));
  }

  public static Dependency of(GroupId groupId, ArtifactId artifactId, DependencyVersion version) {
    return of(coordinates(groupId, artifactId), version);
  }

  public static Dependency of(
      GroupId groupId, ArtifactId artifactId, DependencyVersion version, DependencyScope scope) {
    return of(coordinates(groupId, artifactId), version, scope);
  }
}
