package io.github.bsayli.codegen.initializr.bootstrap;

import io.github.bsayli.codegen.initializr.adapter.out.StandardArtifactsAdapter;
import io.github.bsayli.codegen.initializr.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeansConfig {

  @Bean
  ProjectArtifactsPort projectArtifactsPort(MavenPomPort mavenPomPort) {
    return new StandardArtifactsAdapter(mavenPomPort);
  }
}
