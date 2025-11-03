package io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.build;

import static java.util.Map.entry;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.shared.PomDependency;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.shared.PomDependencyMapper;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.MavenPomPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.ProjectIdentity;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.platform.PlatformTarget;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MavenPomAdapter implements MavenPomPort {

  private static final String KEY_GROUP_ID = "groupId";
  private static final String KEY_ARTIFACT_ID = "artifactId";
  private static final String KEY_JAVA_VERSION = "javaVersion";
  private static final String KEY_SPRING_BOOT_VER = "springBootVersion";
  private static final String KEY_DEPENDENCIES = "dependencies";
  private static final String KEY_PROJECT_NAME = "projectName";
  private static final String KEY_PROJECT_DESCRIPTION = "projectDescription";

  private static final PomDependency CORE_STARTER =
      PomDependency.of("org.springframework.boot", "spring-boot-starter");

  private static final PomDependency TEST_STARTER =
      PomDependency.of("org.springframework.boot", "spring-boot-starter-test", null, "test");

  private final TemplateRenderer renderer;
  private final ArtifactProperties artifactProperties;
  private final PomDependencyMapper pomDependencyMapper;

  public MavenPomAdapter(
      TemplateRenderer renderer,
      ArtifactProperties artifactProperties,
      PomDependencyMapper pomDependencyMapper) {
    this.renderer = renderer;
    this.artifactProperties = artifactProperties;
    this.pomDependencyMapper = pomDependencyMapper;
  }

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.POM;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    Path outPath = Path.of(artifactProperties.outputPath());
    String template = artifactProperties.template();
    Map<String, Object> model = buildModel(blueprint);
    GeneratedFile file = renderer.renderUtf8(outPath, template, model);
    return List.of(file);
  }

  private Map<String, Object> buildModel(ProjectBlueprint bp) {
    ProjectIdentity id = bp.getIdentity();
    PlatformTarget pt = bp.getPlatformTarget();

    List<PomDependency> dependencies = new ArrayList<>();
    dependencies.add(CORE_STARTER);
    dependencies.addAll(pomDependencyMapper.from(bp.getDependencies()));
    dependencies.add(TEST_STARTER);

    return Map.ofEntries(
        entry(KEY_GROUP_ID, id.groupId().value()),
        entry(KEY_ARTIFACT_ID, id.artifactId().value()),
        entry(KEY_JAVA_VERSION, pt.java().asString()),
        entry(KEY_SPRING_BOOT_VER, pt.springBoot().value()),
        entry(KEY_PROJECT_NAME, bp.getName().value()),
        entry(KEY_PROJECT_DESCRIPTION, bp.getDescription().value()),
        entry(KEY_DEPENDENCIES, dependencies));
  }
}
