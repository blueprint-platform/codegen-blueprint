package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ConfigFilesPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ApplicationYamlAdapter implements ConfigFilesPort {

  private static final String KEY_PROJECT_NAME = "projectName";

  private final TemplateRenderer renderer;
  private final ArtifactProperties artifactProperties;

  public ApplicationYamlAdapter(TemplateRenderer renderer, ArtifactProperties artifactProperties) {
    this.renderer = renderer;
    this.artifactProperties = artifactProperties;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    Path outPath = Path.of(artifactProperties.outputPath());
    String template = artifactProperties.template();
    Map<String, Object> model = buildModel(blueprint);
    return List.of(renderer.renderUtf8(outPath, template, model));
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.APPLICATION_YAML;
  }

  private Map<String, Object> buildModel(ProjectBlueprint blueprint) {
    return Map.ofEntries(entry(KEY_PROJECT_NAME, blueprint.getName().value()));
  }
}
