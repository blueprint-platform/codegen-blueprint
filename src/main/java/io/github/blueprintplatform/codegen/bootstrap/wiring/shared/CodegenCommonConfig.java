package io.github.blueprintplatform.codegen.bootstrap.wiring.shared;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependencyMapper;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactPipelineExecutor;
import io.github.blueprintplatform.codegen.adapter.out.shared.templating.FtlClasspathTemplateScanner;
import io.github.blueprintplatform.codegen.adapter.shared.naming.StringCaseFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class CodegenCommonConfig {

  @Bean
  public StringCaseFormatter stringCaseFormatter() {
    return new StringCaseFormatter();
  }

  @Bean
  public BuildDependencyMapper pomDependencyMapper() {
    return new BuildDependencyMapper();
  }

  @Bean
  public FtlClasspathTemplateScanner ftlClasspathTemplateScanner(
      ResourcePatternResolver ftlClasspathTemplateResourcePatternResolver) {
    return new FtlClasspathTemplateScanner(ftlClasspathTemplateResourcePatternResolver);
  }

  @Bean
  public ArtifactPipelineExecutor artifactPipelineExecutor() {
    return new ArtifactPipelineExecutor();
  }

  @Bean
  public ResourcePatternResolver ftlClasspathTemplateResourcePatternResolver() {
    return new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
  }
}
