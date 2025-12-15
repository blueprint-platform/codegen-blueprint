package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.config;

import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.AbstractSingleTemplateArtifactAdapter;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ApplicationConfigurationPort;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ApplicationYamlAdapter extends AbstractSingleTemplateArtifactAdapter
    implements ApplicationConfigurationPort {

  private static final String KEY_APP_NAME = "applicationName";
  private static final String KEY_FEATURES = "features";

  private static final String SPRING_BOOT_GROUP_ID = "org.springframework.boot";

  private static final Feature H2 = new Feature("h2", "spring-boot-starter-data-jpa");
  private static final Feature ACTUATOR = new Feature("actuator", "spring-boot-starter-actuator");
  private static final Feature SECURITY = new Feature("security", "spring-boot-starter-security");

  private static final Feature[] FEATURES = {H2, ACTUATOR, SECURITY};

  public ApplicationYamlAdapter(TemplateRenderer renderer, ArtifactSpec artifactSpec) {
    super(renderer, artifactSpec);
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.APP_CONFIG;
  }

  @Override
  protected Map<String, Object> buildModel(ProjectBlueprint blueprint) {
    ProjectIdentity id = blueprint.getMetadata().identity();

    var deps = blueprint.getDependencies();

    Map<String, Boolean> features =
        deps == null || deps.isEmpty()
            ? Map.of()
            : Arrays.stream(FEATURES)
                .collect(
                    Collectors.toMap(
                        Feature::key, f -> deps.asList().stream().anyMatch(f.predicate())));

    return Map.of(KEY_APP_NAME, id.artifactId().value(), KEY_FEATURES, features);
  }

  private record Feature(String key, String starterArtifactId) {
    Predicate<Dependency> predicate() {
      return d ->
          SPRING_BOOT_GROUP_ID.equalsIgnoreCase(d.coordinates().groupId().value())
              && Objects.equals(starterArtifactId, d.coordinates().artifactId().value());
    }
  }
}
