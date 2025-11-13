package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.out.artifact.AbstractSingleTemplateArtifactAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ConfigFilesPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactDefinition;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import java.util.Map;

public class ApplicationYamlAdapter extends AbstractSingleTemplateArtifactAdapter
    implements ConfigFilesPort {

  private static final String KEY_PROJECT_NAME = "projectName";

  public ApplicationYamlAdapter(TemplateRenderer renderer, ArtifactDefinition artifactDefinition) {
    super(renderer, artifactDefinition);
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.APPLICATION_YAML;
  }

  @Override
  protected Map<String, Object> buildModel(ProjectBlueprint blueprint) {
    return Map.ofEntries(entry(KEY_PROJECT_NAME, blueprint.getName().value()));
  }
}
