package com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot;

import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import com.c9.codegen.initializr.projectgeneration.model.templating.TemplateType;
import com.c9.codegen.initializr.projectgeneration.ports.ApplicationPropertiesGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.TemplateEngine;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("springBootApplicationPropertiesGenerator")
public class SpringBootApplicationPropertiesGenerator implements ApplicationPropertiesGenerator {

  private static final String SRC_MAIN_RESOURCES = "src/main/resources";
  private final TemplateEngine freeMarkerTemplateEngine;

  public SpringBootApplicationPropertiesGenerator(TemplateEngine freeMarkerTemplateEngine) {
    this.freeMarkerTemplateEngine = freeMarkerTemplateEngine;
  }

  @Override
  public void generateApplicationProperties(
      File projectDestination, ProjectMetadata projectMetadata) throws IOException {
    Map<String, Object> appPropertiesModel = new HashMap<>();
    appPropertiesModel.put("projectName", projectMetadata.getName());

    File srcMainResourcesFile = new File(projectDestination, SRC_MAIN_RESOURCES);

    freeMarkerTemplateEngine.generateFileFromTemplate(
        TemplateType.SPRING_BOOT_APPLICATION_PROPERTIES, appPropertiesModel, srcMainResourcesFile);
  }
}
