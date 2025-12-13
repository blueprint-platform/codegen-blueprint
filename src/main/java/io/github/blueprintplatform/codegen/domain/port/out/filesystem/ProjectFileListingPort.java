package io.github.blueprintplatform.codegen.domain.port.out.filesystem;

import java.nio.file.Path;
import java.util.List;

public interface ProjectFileListingPort {
  List<Path> listFiles(Path projectRoot);
}
