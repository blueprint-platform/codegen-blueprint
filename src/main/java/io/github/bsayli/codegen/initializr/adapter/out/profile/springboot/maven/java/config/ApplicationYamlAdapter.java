package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.out.spi.ArtifactGenerator;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ConfigFilesPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ApplicationYamlAdapter implements ConfigFilesPort, ArtifactGenerator {

  private static final String KEY_FRAMEWORK = "framework";
  private static final String KEY_BUILD_TOOL = "buildTool";
  private static final String KEY_LANGUAGE = "language";

  private final TemplateRenderer renderer;
  private final ArtifactProperties artifactProperties;

  public ApplicationYamlAdapter(TemplateRenderer renderer, ArtifactProperties artifactProperties) {
    this.renderer = renderer;
    this.artifactProperties = artifactProperties;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(BuildOptions options) {
    Path outPath = Path.of(artifactProperties.outputPath());
    String template = artifactProperties.template();
    Map<String, Object> model = buildModel(options);
    return List.of(renderer.renderUtf8(outPath, template, model));
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.APPLICATION_YAML;
  }

  @Override
  public Iterable<? extends GeneratedFile> generateFiles(ProjectBlueprint blueprint) {
    return generate(blueprint.getBuildOptions());
  }

  private Map<String, Object> buildModel(BuildOptions options) {
    return Map.ofEntries(
        entry(KEY_FRAMEWORK, options.framework().name()),
        entry(KEY_BUILD_TOOL, options.buildTool().name()),
        entry(KEY_LANGUAGE, options.language().name()));
  }
}
