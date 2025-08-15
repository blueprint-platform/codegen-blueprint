package com.c9.codegen.initializr.projectgeneration.configuration.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "maven.sourcefolder")
public record MavenJavaSourceFolderProperties(
    String srcMainJava,
    String srcMainResources,
    String srcTestJava,
    String srcTestResources,
    String srcGenJava) {

  public List<String> getSourceFolders() {
    return List.of(srcMainJava, srcMainResources, srcTestJava, srcTestResources, srcGenJava);
  }
}
