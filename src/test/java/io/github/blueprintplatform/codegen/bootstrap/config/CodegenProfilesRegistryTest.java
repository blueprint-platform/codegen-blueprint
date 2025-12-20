package io.github.blueprintplatform.codegen.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.bootstrap.config.keys.ProfileKeys;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.ArtifactDefinition;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.CodegenProfilesProperties;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.ProfileProperties;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.TemplateDefinition;
import io.github.blueprintplatform.codegen.bootstrap.config.registry.CodegenProfilesRegistry;
import io.github.blueprintplatform.codegen.bootstrap.error.exception.ProfileConfigurationException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("bootstrap")
class CodegenProfilesRegistryTest {

  private static final String PROFILE_KEY = ProfileKeys.SPRING_BOOT_MAVEN_JAVA;
  private static final ArtifactKey ARTIFACT_KEY = ArtifactKey.BUILD_CONFIG;
  private static final String ARTIFACT_MAP_KEY = ARTIFACT_KEY.key();
  private static final String TEMPLATE_BASE_PATH = "springboot/maven/java/";

  private static CodegenProfilesRegistry registryWithBlankBasePath() {
    TemplateDefinition templateDefinition = new TemplateDefinition("pom.ftl", "pom.xml");
    ArtifactDefinition artifactDefinition =
        new ArtifactDefinition(null, List.of(templateDefinition));

    ProfileProperties profileProperties =
        new ProfileProperties(
            "  ", List.of(ARTIFACT_KEY), Map.of(ARTIFACT_MAP_KEY, artifactDefinition));

    CodegenProfilesProperties properties =
        new CodegenProfilesProperties(Map.of(PROFILE_KEY, profileProperties));

    return new CodegenProfilesRegistry(properties);
  }

  @Test
  @DisplayName(
      "requireArtifact() should return ArtifactDefinition with profile basePath and artifact templates")
  void requireArtifact_shouldReturnDefinitionWithProfileBasePathAndArtifactTemplates() {
    TemplateDefinition templateDefinition = new TemplateDefinition("pom.ftl", "pom.xml");
    ArtifactDefinition artifactDefinition =
        new ArtifactDefinition("artifact-specific/", List.of(templateDefinition));

    ProfileProperties profileProperties =
        new ProfileProperties(
            TEMPLATE_BASE_PATH,
            List.of(ARTIFACT_KEY),
            Map.of(ARTIFACT_MAP_KEY, artifactDefinition));

    CodegenProfilesProperties properties =
        new CodegenProfilesProperties(Map.of(PROFILE_KEY, profileProperties));

    CodegenProfilesRegistry registry = new CodegenProfilesRegistry(properties);

    ArtifactDefinition result = registry.requireArtifact(PROFILE_KEY, ARTIFACT_KEY);

    assertThat(result.basePath()).isEqualTo(TEMPLATE_BASE_PATH);
    assertThat(result.templates()).isSameAs(artifactDefinition.templates());
  }

  @Test
  @DisplayName(
      "requireProfile() should throw ProfileConfigurationException when profile is missing")
  void requireProfile_shouldThrowWhenProfileMissing() {
    CodegenProfilesProperties properties = new CodegenProfilesProperties(Map.of());
    CodegenProfilesRegistry registry = new CodegenProfilesRegistry(properties);

    assertThatThrownBy(() -> registry.requireProfile(PROFILE_KEY))
        .isInstanceOfSatisfying(
            ProfileConfigurationException.class,
            ex -> {
              assertThat(ex.getMessageKey())
                  .isEqualTo(ProfileConfigurationException.KEY_PROFILE_NOT_FOUND);
              assertThat(ex.getArgs()).containsExactly(PROFILE_KEY);
            });
  }

  @Test
  @DisplayName(
      "requireArtifact() should throw ProfileConfigurationException when artifact is missing")
  void requireArtifact_shouldThrowWhenArtifactMissing() {
    ProfileProperties profileProperties =
        new ProfileProperties(TEMPLATE_BASE_PATH, List.of(ARTIFACT_KEY), Map.of());

    CodegenProfilesProperties properties =
        new CodegenProfilesProperties(Map.of(PROFILE_KEY, profileProperties));

    CodegenProfilesRegistry registry = new CodegenProfilesRegistry(properties);

    assertThatThrownBy(() -> registry.requireArtifact(PROFILE_KEY, ARTIFACT_KEY))
        .isInstanceOfSatisfying(
            ProfileConfigurationException.class,
            ex -> {
              assertThat(ex.getMessageKey())
                  .isEqualTo(ProfileConfigurationException.KEY_ARTIFACT_NOT_FOUND);
              assertThat(ex.getArgs()).containsExactly(ARTIFACT_MAP_KEY, PROFILE_KEY);
            });
  }

  @Test
  @DisplayName(
      "requireArtifact() should throw ProfileConfigurationException when templateBasePath is blank")
  void requireArtifact_shouldThrowWhenTemplateBasePathBlank() {
    CodegenProfilesRegistry registry = registryWithBlankBasePath();

    assertThatThrownBy(() -> registry.requireArtifact(PROFILE_KEY, ARTIFACT_KEY))
        .isInstanceOfSatisfying(
            ProfileConfigurationException.class,
            ex -> {
              assertThat(ex.getMessageKey())
                  .isEqualTo(ProfileConfigurationException.KEY_TEMPLATE_BASE_MISSING);
              assertThat(ex.getArgs()).containsExactly(PROFILE_KEY);
            });
  }
}
