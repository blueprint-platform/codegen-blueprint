package io.github.blueprintplatform.codegen.application.port.out.output;

import java.nio.file.Path;
import java.util.List;

public interface ProjectOutputPort {
  List<ProjectOutputItem> list(Path projectRoot);
}
