package io.github.bsayli.codegen.initializr.bootstrap.wiring;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.error.exception.ArtifactKeyMismatchException;
import io.github.bsayli.codegen.initializr.adapter.error.exception.GeneratorFactoryNotFoundException;
import io.github.bsayli.codegen.initializr.adapter.out.ProfileBasedArtifactsSelector;
import io.github.bsayli.codegen.initializr.adapter.out.SpringBootMavenJavaArtifactsAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.build.MavenPomAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config.ApplicationYamlAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.docs.ReadmeAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.vcs.GitIgnoreAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.spi.ArtifactGenerator;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.adapter.profile.ProfileType;
import io.github.bsayli.codegen.initializr.application.port.out.ProjectArtifactsPort;
import io.github.bsayli.codegen.initializr.application.port.out.ProjectArtifactsSelector;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.bootstrap.config.CodegenProfilesProperties;
import io.github.bsayli.codegen.initializr.bootstrap.config.ProfileProperties;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.context.annotation.Bean;

// @Configuration
class ArtifactsWiring {

  private static final Map<ArtifactKey, ArtifactGeneratorFactory> GENERATOR_FACTORIES =
      Map.of(
          ArtifactKey.POM, MavenPomAdapter::new,
          ArtifactKey.GITIGNORE, GitIgnoreAdapter::new,
          ArtifactKey.APPLICATION_YAML, ApplicationYamlAdapter::new,
          ArtifactKey.README, ReadmeAdapter::new);

  @Bean
  ProjectArtifactsPort springbootMavenJavaArtifactsPort(
      TemplateRenderer renderer, CodegenProfilesProperties profiles) {

    var type = ProfileType.SPRINGBOOT_MAVEN_JAVA;
    ProfileProperties profile = profiles.requireProfile(type);

    List<ArtifactGenerator> generators =
        profile.run().stream()
            .map(
                expectedKey -> {
                  var factory = GENERATOR_FACTORIES.get(expectedKey);
                  if (factory == null) {
                    throw new GeneratorFactoryNotFoundException(expectedKey);
                  }
                  var artifactProperties = profiles.artifact(type, expectedKey);
                  var generator = factory.apply(renderer, artifactProperties);
                  var actualKey = generator.artifactKey();
                  if (!actualKey.equals(expectedKey)) {
                    throw new ArtifactKeyMismatchException(expectedKey, actualKey);
                  }
                  return generator;
                })
            .toList();

    return new SpringBootMavenJavaArtifactsAdapter(generators);
  }

  @Bean
  ProjectArtifactsSelector artifactsSelector(
      ProjectArtifactsPort springbootMavenJavaArtifactsPort) {
    Map<ProfileType, ProjectArtifactsPort> registry =
        Map.of(ProfileType.SPRINGBOOT_MAVEN_JAVA, springbootMavenJavaArtifactsPort);
    return new ProfileBasedArtifactsSelector(registry);
  }

  @FunctionalInterface
  interface ArtifactGeneratorFactory
      extends BiFunction<TemplateRenderer, ArtifactProperties, ArtifactGenerator> {}
}
