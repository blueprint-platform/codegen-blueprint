package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependency;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenPomBuildConfigurationAdapter;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.util.ArrayList;
import java.util.List;

public class SpringBootMavenJavaArtifactsAdapter implements ProjectArtifactsPort {

  @FunctionalInterface
  public interface ArtifactStep {
    Iterable<GeneratedResource> generate(
        ProjectBlueprint blueprint, EffectiveDependencies plan);
  }

  public record EffectiveDependencies(List<BuildDependency> dependencies, boolean mvcPresent) {}

  private final MavenPomBuildConfigurationAdapter pom;
  private final List<ArtifactStep> steps;

  public SpringBootMavenJavaArtifactsAdapter(
      MavenPomBuildConfigurationAdapter pom, List<ArtifactStep> steps) {
    this.pom = pom;
    this.steps = List.copyOf(steps);
  }

  @Override
  public Iterable<GeneratedResource> generate(ProjectBlueprint blueprint) {
    var dependencies = pom.effectiveDependencies(blueprint);
    boolean mvc =
        dependencies.stream()
            .anyMatch(
                dependency ->
                    "org.springframework.boot".equals(dependency.groupId())
                        && "spring-boot-starter-webmvc".equals(dependency.artifactId()));
    var plan = new EffectiveDependencies(dependencies, mvc);
    var resources = new ArrayList<GeneratedResource>();
    for (var step : steps) {
      step.generate(blueprint, plan).forEach(resources::add);
    }
    return List.copyOf(resources);
  }
}
