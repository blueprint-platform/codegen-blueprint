package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.util.List;

public interface ProjectPublisherPort {
  PublishedProject publish(
      Path target, String artifactId, Iterable<? extends GeneratedResource> resources);

  record PublishedProject(Path root, Path archive, List<ProjectOutputItem> files) {}
}
