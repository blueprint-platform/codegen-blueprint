package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependencyMapper;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.governance.ArchitectureGovernanceAdapter;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenDependencyPlan;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenPomBuildConfigurationAdapter;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactPort;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SpringBootMavenJavaArtifactsAdapter implements ProjectArtifactsPort {

  private final MavenPomBuildConfigurationAdapter pom;
  private final ArchitectureGovernanceAdapter governance;
  private final BuildDependencyMapper dependencies;
  private final List<ArtifactKey> order;
  private final Map<ArtifactKey, ArtifactPort> standalone;

  public SpringBootMavenJavaArtifactsAdapter(
      MavenPomBuildConfigurationAdapter pom,
      ArchitectureGovernanceAdapter governance,
      BuildDependencyMapper dependencies,
      List<ArtifactKey> order,
      List<ArtifactPort> standalone) {
    this.pom = pom;
    this.governance = governance;
    this.dependencies = dependencies;
    this.order = List.copyOf(order);
    var byKey = new EnumMap<ArtifactKey, ArtifactPort>(ArtifactKey.class);
    for (var artifact : standalone) {
      var key = artifact.artifactKey();
      if (key == ArtifactKey.BUILD_CONFIG
          || key == ArtifactKey.ARCHITECTURE_GOVERNANCE
          || byKey.putIfAbsent(key, artifact) != null) {
        throw new IllegalArgumentException("Duplicate Spring Boot Maven artifact: " + key.key());
      }
    }
    this.standalone = Map.copyOf(byKey);
    for (var key : this.order) {
      if (key != ArtifactKey.BUILD_CONFIG
          && key != ArtifactKey.ARCHITECTURE_GOVERNANCE
          && !this.standalone.containsKey(key)) {
        throw new IllegalArgumentException("Unbound Spring Boot Maven artifact: " + key.key());
      }
    }
  }

  @Override
  public Iterable<GeneratedResource> generate(ProjectBlueprint blueprint) {
    var plan = MavenDependencyPlan.from(blueprint, dependencies);
    var resources = new ArrayList<GeneratedResource>();
    for (var key : order) {
      var generated =
          switch (key) {
            case BUILD_CONFIG -> pom.generate(blueprint, plan);
            case ARCHITECTURE_GOVERNANCE -> governance.generate(blueprint, plan.mvcPresent());
            default -> standalone.get(key).generate(blueprint);
          };
      generated.forEach(resources::add);
    }
    return List.copyOf(resources);
  }
}
