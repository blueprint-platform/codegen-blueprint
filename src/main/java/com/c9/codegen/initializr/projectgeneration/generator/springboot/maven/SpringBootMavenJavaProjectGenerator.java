package com.c9.codegen.initializr.projectgeneration.generator.springboot.maven;

import com.c9.codegen.initializr.projectgeneration.generator.base.AbstractProjectGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ApplicationPropertiesGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.FrameworkProjectStarterClassGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.FrameworkSpecificTestUnitGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.GitIgnoreGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectArchiver;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectBuildGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectDirectoryInitializer;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectDocumentationGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectLayoutGenerator;
import org.springframework.stereotype.Component;

@Component("springBootMavenJavaProjectGenerator")
public class SpringBootMavenJavaProjectGenerator extends AbstractProjectGenerator {

  public SpringBootMavenJavaProjectGenerator(
      ProjectDirectoryInitializer projectRootDirectoryInitializer,
      GitIgnoreGenerator gitIgnoreFileGenerator,
      ProjectArchiver projectZipArchiver,
      ProjectLayoutGenerator mavenJavaProjectLayoutGenerator,
      ProjectBuildGenerator springBootMavenJavaProjectBuildGenerator,
      ApplicationPropertiesGenerator springBootApplicationPropertiesGenerator,
      FrameworkProjectStarterClassGenerator springBootJavaMainClassGenerator,
      FrameworkSpecificTestUnitGenerator springBootJavaTestClassGenerator,
      ProjectDocumentationGenerator springBootMavenJavaReadMeGenerator) {
    super(
        projectRootDirectoryInitializer,
        gitIgnoreFileGenerator,
        projectZipArchiver,
        mavenJavaProjectLayoutGenerator,
        springBootMavenJavaProjectBuildGenerator,
        springBootApplicationPropertiesGenerator,
        springBootJavaMainClassGenerator,
        springBootJavaTestClassGenerator,
        springBootMavenJavaReadMeGenerator);
  }
}
