package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.docs;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.out.build.maven.shared.PomDependency;
import io.github.bsayli.codegen.initializr.adapter.out.build.maven.shared.PomDependencyMapper;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactKey;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ReadmePort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.Dependencies;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.ProjectIdentity;
import io.github.bsayli.codegen.initializr.domain.model.value.pkg.PackageName;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.PlatformTarget;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildOptions;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ReadmeAdapter implements ReadmePort {

  private static final String KEY_PROJECT_NAME = "projectName";
  private static final String KEY_PROJECT_DESCRIPTION = "projectDescription";
  private static final String KEY_GROUP_ID = "groupId";
  private static final String KEY_ARTIFACT_ID = "artifactId";
  private static final String KEY_PACKAGE_NAME = "packageName";
  private static final String KEY_BUILD_TOOL = "buildTool";
  private static final String KEY_LANGUAGE = "language";
  private static final String KEY_FRAMEWORK = "framework";
  private static final String KEY_JAVA_VERSION = "javaVersion";
  private static final String KEY_SPRING_BOOT_VERSION = "springBootVersion";
  private static final String KEY_DEPENDENCIES = "dependencies";

  private final TemplateRenderer renderer;
  private final ArtifactProperties artifactProperties;
  private final PomDependencyMapper pomDependencyMapper;

  public ReadmeAdapter(
      TemplateRenderer renderer,
      ArtifactProperties artifactProperties,
      PomDependencyMapper pomDependencyMapper) {
    this.renderer = renderer;
    this.artifactProperties = artifactProperties;
    this.pomDependencyMapper = pomDependencyMapper;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    Path outPath = Path.of(artifactProperties.outputPath());
    String template = artifactProperties.template();
    Map<String, Object> model = buildModel(blueprint);
    GeneratedFile generatedFile = renderer.renderUtf8(outPath, template, model);
    return List.of(generatedFile);
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.README;
  }

  private Map<String, Object> buildModel(ProjectBlueprint bp) {
    ProjectIdentity id = bp.getIdentity();
    BuildOptions bo = bp.getBuildOptions();
    PlatformTarget pt = bp.getPlatformTarget();
    PackageName pn = bp.getPackageName();
    Dependencies selectedDependencies = bp.getDependencies();

    List<PomDependency> dependencies = pomDependencyMapper.from(selectedDependencies);

    return Map.ofEntries(
        entry(KEY_PROJECT_NAME, bp.getName().value()),
        entry(KEY_PROJECT_DESCRIPTION, bp.getDescription().value()),
        entry(KEY_GROUP_ID, id.groupId().value()),
        entry(KEY_ARTIFACT_ID, id.artifactId().value()),
        entry(KEY_PACKAGE_NAME, pn.value()),
        entry(KEY_BUILD_TOOL, bo.buildTool().name()),
        entry(KEY_LANGUAGE, bo.language().name()),
        entry(KEY_FRAMEWORK, bo.framework().name()),
        entry(KEY_JAVA_VERSION, pt.java().asString()),
        entry(KEY_SPRING_BOOT_VERSION, pt.springBoot().value()),
        entry(KEY_DEPENDENCIES, dependencies));
  }
}
