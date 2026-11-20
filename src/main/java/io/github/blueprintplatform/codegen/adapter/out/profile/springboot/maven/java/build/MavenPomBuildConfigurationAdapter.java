package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build;

import static java.util.Map.entry;

import io.github.blueprintplatform.codegen.adapter.error.exception.templating.InvalidMavenMetadataException;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class MavenPomBuildConfigurationAdapter {

  private final TemplateRenderer renderer;
  private final ArtifactSpec artifactSpec;

  public MavenPomBuildConfigurationAdapter(TemplateRenderer renderer, ArtifactSpec artifactSpec) {
    this.renderer = renderer;
    this.artifactSpec = artifactSpec;
  }

  public Iterable<GeneratedResource> generate(ProjectBlueprint bp, MavenDependencyPlan plan) {
    var template = artifactSpec.templates().getFirst();
    return List.of(
        renderer.renderUtf8(
            Path.of(template.outputPath()),
            artifactSpec.basePath() + template.template(),
            buildModel(bp, plan)));
  }

  private Map<String, Object> buildModel(ProjectBlueprint bp, MavenDependencyPlan plan) {
    requireLiteral("name", bp.getMetadata().name().value());
    requireLiteral("description", bp.getMetadata().description().value());
    ProjectIdentity id = bp.getMetadata().identity();
    SpringBootJvmTarget target = (SpringBootJvmTarget) bp.getPlatform().platformTarget();

    return Map.ofEntries(
        entry(MavenPomBuildModel.KEY_GROUP_ID, id.groupId().value()),
        entry(MavenPomBuildModel.KEY_ARTIFACT_ID, id.artifactId().value()),
        entry(MavenPomBuildModel.KEY_JAVA_VERSION, target.java().asString()),
        entry(MavenPomBuildModel.KEY_SPRING_BOOT_VER, target.patchVersion()),
        entry(MavenPomBuildModel.KEY_PROJECT_NAME, bp.getMetadata().name().value()),
        entry(MavenPomBuildModel.KEY_PROJECT_DESCRIPTION, bp.getMetadata().description().value()),
        entry(MavenPomBuildModel.KEY_POM_PROPERTIES, governancePomProperties(bp)),
        entry(MavenPomBuildModel.KEY_DEPENDENCIES, plan.dependencies()),
        entry("sampleEnabled", plan.sampleEnabled()));
  }

  private Map<String, String> governancePomProperties(ProjectBlueprint bp) {
    if (!bp.getArchitecture().governance().isEnabled()) {
      return Map.of();
    }
    return Map.of(MavenPomBuildModel.ARCH_UNIT_VERSION_KEY, MavenPomBuildModel.ARCH_UNIT_VERSION);
  }

  private static void requireLiteral(String field, String value) {
    if (value.contains("${")
        || value
            .codePoints()
            .anyMatch(
                cp ->
                    !(cp == 9
                        || cp == 10
                        || cp == 13
                        || cp >= 32 && cp <= 0xD7FF
                        || cp >= 0xE000 && cp <= 0xFFFD
                        || cp >= 0x10000))) {
      throw new InvalidMavenMetadataException(field);
    }
  }
}
