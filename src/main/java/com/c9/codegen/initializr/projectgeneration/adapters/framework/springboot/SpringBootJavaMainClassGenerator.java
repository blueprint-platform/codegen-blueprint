package com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot;

import static com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot.constants.SpringBootJavaMainClassGeneratorConstants.FILE_NAME_EXTENSION;
import static com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot.constants.SpringBootJavaMainClassGeneratorConstants.FILE_NAME_POSTFIX;
import static com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot.constants.SpringBootJavaMainClassGeneratorConstants.TEMPLATE_NAME;

import com.c9.codegen.initializr.projectgeneration.configuration.properties.MavenJavaSourceFolderProperties;
import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import com.c9.codegen.initializr.projectgeneration.ports.FrameworkProjectStarterClassGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.TemplateEngine;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("springBootJavaMainClassGenerator")
public class SpringBootJavaMainClassGenerator implements FrameworkProjectStarterClassGenerator {

  private final TemplateEngine freeMarkerTemplateEngine;
  private final MavenJavaSourceFolderProperties sourceFolder;

  public SpringBootJavaMainClassGenerator(
      TemplateEngine freeMarkerTemplateEngine, MavenJavaSourceFolderProperties sourceFolder) {
    this.sourceFolder = sourceFolder;
    this.freeMarkerTemplateEngine = freeMarkerTemplateEngine;
  }

  @Override
  public void generateProjectStarterClass(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException {

    Map<String, Object> mainClassModel = new HashMap<>();
    mainClassModel.put("projectPackageName", projectMetadata.getPackageName());

    String className = sanitizeClassName(projectMetadata.getName());
    className =
        className.substring(0, 1).toUpperCase() + className.substring(1) + FILE_NAME_POSTFIX;
    mainClassModel.put("className", className);

    String basePackagePath = projectMetadata.getPackageName().replace(".", "/");
    File srcMainJavaFile = new File(projectDestination, sourceFolder.srcMainJava());

    File mainClassFileDestination = new File(srcMainJavaFile, basePackagePath);
    String fileName = className + FILE_NAME_EXTENSION;

    freeMarkerTemplateEngine.generateFileFromTemplate(
        TEMPLATE_NAME, fileName, mainClassModel, mainClassFileDestination);
  }

  private String sanitizeClassName(String className) {
    StringBuilder sanitizedName = new StringBuilder();
    className
        .chars()
        .forEach(
            c -> {
              if (Character.isLetterOrDigit(c) || c == '_') {
                sanitizedName.append((char) c);
              }
            });
    return sanitizedName.toString();
  }
}
