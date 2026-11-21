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
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.support.ResourceBundleMessageSource;

@Tag("unit")
@Tag("bootstrap")
class CodegenProfilesRegistryTest {

  private static final String PROFILE_KEY = ProfileKeys.SPRING_BOOT_MAVEN_JAVA;
  private static final ArtifactKey ARTIFACT_KEY = ArtifactKey.BUILD_CONFIG;
  private static final String ARTIFACT_MAP_KEY = ARTIFACT_KEY.key();

  private static CodegenProfilesRegistry registryWithArtifact(
      ArtifactDefinition artifactDefinition) {
    ProfileProperties profileProperties =
        new ProfileProperties(List.of(ARTIFACT_KEY), Map.of(ARTIFACT_MAP_KEY, artifactDefinition));

    CodegenProfilesProperties properties =
        new CodegenProfilesProperties(Map.of(PROFILE_KEY, profileProperties));

    return new CodegenProfilesRegistry(properties);
  }

  @Test
  @DisplayName("requireArtifact() should return ArtifactDefinition as-is when present")
  void requireArtifact_shouldReturnArtifactDefinition() {
    TemplateDefinition templateDefinition = new TemplateDefinition("pom.ftl", "pom.xml");
    ArtifactDefinition artifactDefinition =
        new ArtifactDefinition("artifact-specific/", List.of(templateDefinition));

    CodegenProfilesRegistry registry = registryWithArtifact(artifactDefinition);

    ArtifactDefinition result = registry.requireArtifact(PROFILE_KEY, ARTIFACT_KEY);

    assertThat(result).isSameAs(artifactDefinition);
    assertThat(result.templateBasePath()).isEqualTo("artifact-specific/");
    assertThat(result.templates()).containsExactly(templateDefinition);
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
    ProfileProperties profileProperties = new ProfileProperties(List.of(ARTIFACT_KEY), Map.of());

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
  void acceptsTemplateFreeSourceLayoutAndDynamicTemplateRoots() {
    var source = new ArtifactDefinition(null, List.of());
    var dynamic = new ArtifactDefinition("springboot/java/", List.of());
    var profile =
        new ProfileProperties(
            List.of(ArtifactKey.SOURCE_LAYOUT, ArtifactKey.ARCHITECTURE_GOVERNANCE,
                ArtifactKey.SAMPLE_CODE),
            Map.of(
                ArtifactKey.SOURCE_LAYOUT.key(), source,
                ArtifactKey.ARCHITECTURE_GOVERNANCE.key(), dynamic,
                ArtifactKey.SAMPLE_CODE.key(), dynamic));
    assertThat(registry(profile).requireValidatedProfile(PROFILE_KEY)).isSameAs(profile);
  }

  @Test
  void rejectsEmptyOrderAndDuplicateArtifactKeys() {
    var pom = new ArtifactDefinition("springboot/maven/java/", List.of(template("pom.xml")));
    var definitions = Map.of(ARTIFACT_MAP_KEY, pom);
    assertInvalid(new ProfileProperties(List.of(), definitions), "bootstrap.profile.artifact-order.empty");
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY, ARTIFACT_KEY), definitions),
        "bootstrap.profile.artifact-order.duplicate");
  }

  @Test
  void rejectsMissingOrderedArtifactAndUnknownDefinition() {
    var pom = new ArtifactDefinition("springboot/maven/java/", List.of(template("pom.xml")));
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY), Map.of("typo", pom)),
        ProfileConfigurationException.KEY_ARTIFACT_NOT_FOUND);
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY), Map.of(ARTIFACT_MAP_KEY, pom, "typo", pom)),
        "bootstrap.profile.artifact.unknown");
  }

  @Test
  void enforcesSingleTemplateAndKeepsDynamicTemplatesUnlisted() {
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY),
            Map.of(ARTIFACT_MAP_KEY, new ArtifactDefinition("pom/", List.of()))),
        "bootstrap.artifact.templates.count");
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY),
            Map.of(ARTIFACT_MAP_KEY,
                new ArtifactDefinition("pom/", List.of(template("pom.xml"), template("other.xml"))))),
        "bootstrap.artifact.templates.count");
    assertInvalid(
        profileWith(ArtifactKey.SAMPLE_CODE,
            new ArtifactDefinition("springboot/java/", List.of(template("unused.java")))),
        "bootstrap.artifact.templates.count");
  }

  @Test
  void rejectsDuplicateOutputAndIncompleteWrapper() {
    var wrapper = ArtifactKey.BUILD_TOOL_FILES;
    assertInvalid(
        profileWith(wrapper,
            new ArtifactDefinition("wrapper/", List.of(template("mvnw"), template("mvnw")))),
        "bootstrap.artifact.template.output.duplicate");
    assertInvalid(
        profileWith(wrapper, new ArtifactDefinition("wrapper/", List.of(template("mvnw")))),
        "bootstrap.artifact.wrapper.outputs.missing");

    var output = new ArtifactDefinition("templates/", List.of(template("pom.xml")));
    assertInvalid(
        new ProfileProperties(List.of(ARTIFACT_KEY, ArtifactKey.APP_CONFIG),
            Map.of(ARTIFACT_MAP_KEY, output, ArtifactKey.APP_CONFIG.key(), output)),
        "bootstrap.profile.output.duplicate");
  }

  @Test
  void rejectsAStaticFileUsedAsAnOutputParent() {
    var parent = new ArtifactDefinition("pom/", List.of(template("output")));
    var child = new ArtifactDefinition("app/", List.of(template("output/app.yml")));
    var profile =
        new ProfileProperties(
            List.of(ARTIFACT_KEY, ArtifactKey.APP_CONFIG),
            Map.of(ARTIFACT_MAP_KEY, parent, ArtifactKey.APP_CONFIG.key(), child));

    assertInvalid(profile, "bootstrap.profile.output.file-parent");
  }

  @Test
  void rejectsOutputTraversalAtBootstrap() {
    assertInvalid(
        profileWith(ARTIFACT_KEY, new ArtifactDefinition("pom/", List.of(template("../pom.xml")))),
        "bootstrap.artifact.template.path.invalid");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "   ")
  void resolvesMissingBasePathWithProfileThenArtifact(String basePath) {
    var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    var profile = profileWith(ARTIFACT_KEY,
        new ArtifactDefinition(basePath, List.of(template("pom.xml"))));
    assertThatThrownBy(() -> registry(profile).requireValidatedProfile(PROFILE_KEY))
        .isInstanceOfSatisfying(
            ProfileConfigurationException.class,
            failure -> {
              assertThat(failure.getMessageKey())
                  .isEqualTo(ProfileConfigurationException.KEY_TEMPLATE_BASE_MISSING);
              assertThat(failure.getArgs()).containsExactly(PROFILE_KEY, ARTIFACT_MAP_KEY);
              assertThat(messages.getMessage(failure.getMessageKey(), failure.getArgs(), Locale.ENGLISH))
                  .isEqualTo("template-base-path must be set for profile "
                      + PROFILE_KEY + ", artifact " + ARTIFACT_MAP_KEY + ".");
            });
  }

  @Test
  void requiresBasePathForDynamicScanEvenWithoutFixedTemplates() {
    assertInvalid(
        profileWith(ArtifactKey.ARCHITECTURE_GOVERNANCE, new ArtifactDefinition(null, List.of())),
        ProfileConfigurationException.KEY_TEMPLATE_BASE_MISSING);
  }

  private static TemplateDefinition template(String output) {
    return new TemplateDefinition(output + ".ftl", output);
  }

  private static ProfileProperties profileWith(ArtifactKey key, ArtifactDefinition artifact) {
    return new ProfileProperties(List.of(key), Map.of(key.key(), artifact));
  }

  private static CodegenProfilesRegistry registry(ProfileProperties profile) {
    return new CodegenProfilesRegistry(new CodegenProfilesProperties(Map.of(PROFILE_KEY, profile)));
  }

  private static void assertInvalid(ProfileProperties profile, String messageKey) {
    assertThatThrownBy(() -> registry(profile).requireValidatedProfile(PROFILE_KEY))
        .isInstanceOf(ProfileConfigurationException.class)
        .hasMessage(messageKey);
  }
}
