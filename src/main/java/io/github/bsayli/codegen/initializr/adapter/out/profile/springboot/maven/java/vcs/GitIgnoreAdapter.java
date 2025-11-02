package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.vcs;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.GitIgnorePort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class GitIgnoreAdapter implements GitIgnorePort {

  private static final String KEY_IGNORE_LIST = "ignoreList";
  private final TemplateRenderer renderer;
  private final ArtifactProperties artifactProperties;

  public GitIgnoreAdapter(TemplateRenderer renderer, ArtifactProperties artifactProperties) {
    this.renderer = renderer;
    this.artifactProperties = artifactProperties;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint projectBlueprint) {
    Path outPath = Path.of(artifactProperties.outputPath());
    String template = artifactProperties.template();
    Map<String, Object> model = buildModel(projectBlueprint.getBuildOptions());
    GeneratedFile generatedFile = renderer.renderUtf8(outPath, template, model);
    return List.of(generatedFile);
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.GITIGNORE;
  }

  @SuppressWarnings("unused")
  private Map<String, Object> buildModel(BuildOptions buildOptions) {
    return Map.of(KEY_IGNORE_LIST, List.of());
  }
}
