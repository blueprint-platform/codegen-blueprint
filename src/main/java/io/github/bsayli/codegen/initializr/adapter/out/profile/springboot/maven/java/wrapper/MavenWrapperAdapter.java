package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.wrapper;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.out.shared.artifact.AbstractSingleTemplateArtifactAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifact.BuildToolMetadataPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactDefinition;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import java.util.Map;

public class MavenWrapperAdapter extends AbstractSingleTemplateArtifactAdapter
    implements BuildToolMetadataPort {

  private static final String KEY_WRAPPER_VERSION = "wrapperVersion";
  private static final String KEY_MAVEN_VERSION = "mavenVersion";

  private static final String DEFAULT_WRAPPER_VERSION = "3.3.3";
  private static final String DEFAULT_MAVEN_VERSION = "3.9.11";

  public MavenWrapperAdapter(TemplateRenderer renderer, ArtifactDefinition artifactDefinition) {
    super(renderer, artifactDefinition);
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.MAVEN_WRAPPER;
  }

  @Override
  protected Map<String, Object> buildModel(ProjectBlueprint blueprint) {
    return Map.ofEntries(
        entry(KEY_WRAPPER_VERSION, DEFAULT_WRAPPER_VERSION),
        entry(KEY_MAVEN_VERSION, DEFAULT_MAVEN_VERSION));
  }
}
