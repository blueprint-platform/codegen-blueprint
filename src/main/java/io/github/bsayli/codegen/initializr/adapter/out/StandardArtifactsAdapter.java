package io.github.bsayli.codegen.initializr.adapter.out;

import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.MavenPomPort;
import io.github.bsayli.codegen.initializr.domain.port.out.ProjectArtifactsPort;
import io.github.bsayli.codegen.initializr.domain.port.out.artifact.GeneratedFile;
import java.util.List;

public class StandardArtifactsAdapter implements ProjectArtifactsPort {

  private final MavenPomPort mavenPomPort;

  public StandardArtifactsAdapter(MavenPomPort mavenPomPort) {
    this.mavenPomPort = mavenPomPort;
  }

  @Override
  public Iterable<? extends GeneratedFile> generate(ProjectBlueprint blueprint) {
    return List.of(mavenPomPort.generate(blueprint));
  }
}
