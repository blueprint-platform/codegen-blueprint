package io.github.bsayli.codegen.initializr.bootstrap.wiring;

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
import io.github.bsayli.codegen.initializr.bootstrap.config.CodegenProfilesProperties;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;

// @Configuration
class ArtifactsWiring {

  @Bean
  ProjectArtifactsPort springbootMavenJavaArtifactsPort(
      TemplateRenderer renderer, CodegenProfilesProperties profiles) {
    List<ArtifactGenerator> gens =
        List.of(
            new MavenPomAdapter(renderer, profiles),
            new GitIgnoreAdapter(renderer, profiles),
            new ApplicationYamlAdapter(renderer, profiles),
            new ReadmeAdapter(renderer, profiles));
    return new SpringBootMavenJavaArtifactsAdapter(gens);
  }

  @Bean
  ProjectArtifactsSelector artifactsSelector(
      ProjectArtifactsPort springbootMavenJavaArtifactsPort) {
    Map<ProfileType, ProjectArtifactsPort> reg =
        Map.of(ProfileType.SPRINGBOOT_MAVEN_JAVA, springbootMavenJavaArtifactsPort);
    return new ProfileBasedArtifactsSelector(reg);
  }
}
