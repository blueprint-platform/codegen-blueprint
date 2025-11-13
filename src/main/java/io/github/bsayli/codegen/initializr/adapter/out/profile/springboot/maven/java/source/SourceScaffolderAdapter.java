package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.source;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.adapter.shared.naming.StringCaseFormatter;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.SourceScaffolderPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactDefinition;
import io.github.bsayli.codegen.initializr.bootstrap.config.TemplateDefinition;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.ProjectIdentity;
import io.github.bsayli.codegen.initializr.domain.model.value.pkg.PackageName;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class SourceScaffolderAdapter implements SourceScaffolderPort {

  public static final String POSTFIX_APPLICATION = "Application";
  private static final String KEY_PROJECT_PACKAGE = "projectPackageName";
  private static final String KEY_CLASS_NAME = "className";
  private static final String JAVA_FILE_EXTENSION = ".java";
  private static final String PACKAGE_PATH_DELIMITER = ".";
  private static final String FILE_PATH_DELIMITER = "/";
  private final TemplateRenderer renderer;
  private final ArtifactDefinition artifactDefinition;
  private final StringCaseFormatter stringCaseFormatter;

  public SourceScaffolderAdapter(
      TemplateRenderer renderer,
      ArtifactDefinition artifactDefinition,
      StringCaseFormatter stringCaseFormatter) {
    this.renderer = renderer;
    this.artifactDefinition = artifactDefinition;
    this.stringCaseFormatter = stringCaseFormatter;
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.SOURCE_SCAFFOLDER;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    PackageName packageName = blueprint.getPackageName();
    ProjectIdentity id = blueprint.getIdentity();

    String className =
        stringCaseFormatter.toPascalCase(id.artifactId().value()) + POSTFIX_APPLICATION;

    Map<String, Object> model =
        Map.ofEntries(
            entry(KEY_PROJECT_PACKAGE, packageName.value()), entry(KEY_CLASS_NAME, className));

    TemplateDefinition templateDefinition = artifactDefinition.templates().getFirst();
    Path baseDir = Path.of(templateDefinition.outputPath());
    String template = templateDefinition.template();

    String packagePath = packageName.value().replace(PACKAGE_PATH_DELIMITER, FILE_PATH_DELIMITER);
    Path outPath = baseDir.resolve(packagePath).resolve(className + JAVA_FILE_EXTENSION);

    GeneratedFile file = renderer.renderUtf8(outPath, template, model);
    return List.of(file);
  }
}
