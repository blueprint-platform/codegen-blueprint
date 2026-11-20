package io.github.blueprintplatform.codegen.bootstrap.wiring.out.profile;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependencyMapper;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.config.ApplicationYamlAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.doc.ProjectDocumentationAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.governance.ArchitectureGovernanceAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.ignore.GitIgnoreAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.sample.SampleCodeAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.source.MainSourceEntrypointAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.source.SourceLayoutAdapter;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.source.TestSourceEntrypointAdapter;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.SpringBootMavenJavaArtifactsAdapter;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenPomBuildConfigurationAdapter;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.wrapper.MavenWrapperBuildToolFilesAdapter;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.shared.templating.FtlClasspathTemplateScanner;
import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.adapter.shared.naming.StringCaseFormatter;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactPort;
import io.github.blueprintplatform.codegen.bootstrap.config.keys.ProfileKeys;
import io.github.blueprintplatform.codegen.bootstrap.config.registry.CodegenProfilesRegistry;
import io.github.blueprintplatform.codegen.bootstrap.mapper.ArtifactSpecMapper;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootMavenJavaConfig {

  private static final String PROFILE_KEY = ProfileKeys.SPRING_BOOT_MAVEN_JAVA;

  @Bean
  SpringBootMavenJavaArtifactsAdapter springBootMavenJavaArtifactsAdapter(
      TemplateRenderer renderer,
      CodegenProfilesRegistry registry,
      ArtifactSpecMapper mapper,
      BuildDependencyMapper dependencies,
      StringCaseFormatter names,
      FtlClasspathTemplateScanner scanner) {
    var pom =
        new MavenPomBuildConfigurationAdapter(
            renderer, spec(registry, mapper, ArtifactKey.BUILD_CONFIG));
    var governance =
        new ArchitectureGovernanceAdapter(
            renderer, spec(registry, mapper, ArtifactKey.ARCHITECTURE_GOVERNANCE), scanner);
    List<ArtifactPort> standalone =
        List.of(
            new MavenWrapperBuildToolFilesAdapter(
                renderer, spec(registry, mapper, ArtifactKey.BUILD_TOOL_FILES)),
            new GitIgnoreAdapter(renderer, spec(registry, mapper, ArtifactKey.IGNORE_RULES)),
            new SourceLayoutAdapter(),
            new ApplicationYamlAdapter(renderer, spec(registry, mapper, ArtifactKey.APP_CONFIG)),
            new MainSourceEntrypointAdapter(
                renderer, spec(registry, mapper, ArtifactKey.MAIN_SOURCE_ENTRY_POINT), names),
            new TestSourceEntrypointAdapter(
                renderer, spec(registry, mapper, ArtifactKey.TEST_SOURCE_ENTRY_POINT), names),
            new SampleCodeAdapter(
                renderer, spec(registry, mapper, ArtifactKey.SAMPLE_CODE), scanner),
            new ProjectDocumentationAdapter(
                renderer, spec(registry, mapper, ArtifactKey.PROJECT_DOCUMENTATION), dependencies));
    return new SpringBootMavenJavaArtifactsAdapter(
        pom,
        governance,
        dependencies,
        registry.requireProfile(PROFILE_KEY).orderedArtifactKeys(),
        standalone);
  }

  private static ArtifactSpec spec(
      CodegenProfilesRegistry registry, ArtifactSpecMapper mapper, ArtifactKey key) {
    return mapper.from(registry.requireArtifact(PROFILE_KEY, key));
  }
}
