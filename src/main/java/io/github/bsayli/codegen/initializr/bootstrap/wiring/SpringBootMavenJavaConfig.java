package io.github.bsayli.codegen.initializr.bootstrap.wiring;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.error.exception.ArtifactKeyMismatchException;
import io.github.bsayli.codegen.initializr.adapter.out.SpringBootMavenJavaArtifactsAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.build.MavenPomAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config.ApplicationYamlAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.docs.ReadmeAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.shared.PomDependencyMapper;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.vcs.GitIgnoreAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.adapter.profile.ProfileType;
import io.github.bsayli.codegen.initializr.application.port.out.ProjectArtifactsPort;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.bootstrap.config.CodegenProfilesProperties;
import io.github.bsayli.codegen.initializr.bootstrap.error.ProfileConfigurationException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootMavenJavaConfig {

  @Bean
  PomDependencyMapper pomDependencyMapper() {
    return new PomDependencyMapper();
  }

  @Bean
  MavenPomAdapter springBootMavenJavaPomAdapter(
      TemplateRenderer renderer,
      CodegenProfilesProperties profiles,
      PomDependencyMapper pomMapper) {
    ArtifactProperties props =
        profiles.artifact(ProfileType.SPRINGBOOT_MAVEN_JAVA, ArtifactKey.POM);
    return new MavenPomAdapter(renderer, props, pomMapper);
  }

  @Bean
  GitIgnoreAdapter springBootMavenJavaGitIgnoreAdapter(
      TemplateRenderer renderer, CodegenProfilesProperties profiles) {
    ArtifactProperties props =
        profiles.artifact(ProfileType.SPRINGBOOT_MAVEN_JAVA, ArtifactKey.GITIGNORE);
    return new GitIgnoreAdapter(renderer, props);
  }

  @Bean
  ApplicationYamlAdapter springBootMavenJavaApplicationYamlAdapter(
      TemplateRenderer renderer, CodegenProfilesProperties profiles) {
    ArtifactProperties props =
        profiles.artifact(ProfileType.SPRINGBOOT_MAVEN_JAVA, ArtifactKey.APPLICATION_YAML);
    return new ApplicationYamlAdapter(renderer, props);
  }

  @Bean
  ReadmeAdapter springBootMavenJavaReadmeAdapter(
      TemplateRenderer renderer,
      CodegenProfilesProperties profiles,
      PomDependencyMapper pomDependencyMapper) {
    ArtifactProperties props =
        profiles.artifact(ProfileType.SPRINGBOOT_MAVEN_JAVA, ArtifactKey.README);
    return new ReadmeAdapter(renderer, props, pomDependencyMapper);
  }

  @Bean(name = "springBootMavenJavaArtifactRegistry")
  Map<ArtifactKey, ArtifactPort> springBootMavenJavaArtifactRegistry(
      MavenPomAdapter mavenPomAdapter,
      GitIgnoreAdapter gitIgnoreAdapter,
      ApplicationYamlAdapter applicationYamlAdapter,
      ReadmeAdapter readmeAdapter) {

    Map<ArtifactKey, ArtifactPort> registry = new EnumMap<>(ArtifactKey.class);
    registry.put(ArtifactKey.POM, mavenPomAdapter);
    registry.put(ArtifactKey.GITIGNORE, gitIgnoreAdapter);
    registry.put(ArtifactKey.APPLICATION_YAML, applicationYamlAdapter);
    registry.put(ArtifactKey.README, readmeAdapter);
    return registry;
  }

  @Bean
  ProjectArtifactsPort springBootMavenJavaArtifactsAdapter(
      CodegenProfilesProperties profiles,
      @Qualifier("springBootMavenJavaArtifactRegistry") Map<ArtifactKey, ArtifactPort> registry) {

    var profile = profiles.requireProfile(ProfileType.SPRINGBOOT_MAVEN_JAVA);
    var orderedArtifactKeys = profile.orderedArtifactKeys();

    List<ArtifactPort> ordered =
        orderedArtifactKeys.stream()
            .map(
                key -> {
                  ArtifactPort port = registry.get(key);
                  if (port == null) {
                    throw new ProfileConfigurationException(
                        "bootstrap.artifact.not.found",
                        key.key(),
                        ProfileType.SPRINGBOOT_MAVEN_JAVA.key());
                  }
                  if (!port.artifactKey().equals(key)) {
                    throw new ArtifactKeyMismatchException(key, port.artifactKey());
                  }
                  return port;
                })
            .toList();

    return new SpringBootMavenJavaArtifactsAdapter(ordered);
  }
}
