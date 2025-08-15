package com.c9.codegen.initializr.projectgeneration.ports;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GitIgnoreGenerator {

  void generateGitIgnoreContent(File projectDestination, Optional<List<String>> ignoreList)
      throws IOException;
}
