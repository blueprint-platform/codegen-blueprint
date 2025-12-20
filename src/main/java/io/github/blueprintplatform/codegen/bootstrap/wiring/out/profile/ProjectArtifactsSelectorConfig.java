package io.github.blueprintplatform.codegen.bootstrap.wiring.out.profile;

import io.github.blueprintplatform.codegen.adapter.out.profile.ProfileBasedArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.bootstrap.config.keys.ProfileKeys;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectArtifactsSelectorConfig {

  @Bean
  public Map<String, ProjectArtifactsPort> projectArtifactsPortRegistry(
      ProjectArtifactsPort springBootMavenJavaArtifactsAdapter) {
    Map<String, ProjectArtifactsPort> registry = new HashMap<>();
    registry.put(ProfileKeys.SPRING_BOOT_MAVEN_JAVA, springBootMavenJavaArtifactsAdapter);
    return Collections.unmodifiableMap(registry);
  }

  @Bean
  public ProjectArtifactsSelector projectArtifactsSelector(
      Map<String, ProjectArtifactsPort> projectArtifactsPortRegistry) {
    return new ProfileBasedArtifactsSelector(projectArtifactsPortRegistry);
  }
}
