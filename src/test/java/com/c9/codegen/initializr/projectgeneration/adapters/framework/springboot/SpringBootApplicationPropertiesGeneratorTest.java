package com.c9.codegen.initializr.projectgeneration.adapters.framework.springboot;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.c9.codegen.initializr.projectgeneration.adapters.templating.FreeMarkerTemplateEngine;
import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootApplicationPropertiesGeneratorTest {

  private static final String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
  private static final String EXPECTED_APPLICATION_NAME = "codegen-demo";
  private static final String SRC_MAIN_RESOURCES = "src/main/resources";

  @Autowired private SpringBootApplicationPropertiesGenerator applicationPropertiesGenerator;

  @TempDir private Path tempFolder;

  @Test
  void testGenerateApplicationProperties_CreatesCorrectFileStructureAndFileName()
      throws IOException {
    ProjectMetadata projectMetadata =
        new ProjectMetadata.ProjectMetadataBuilder().name("codegen-demo").build();

    File projectDestination = tempFolder.toFile();

    applicationPropertiesGenerator.generateApplicationProperties(
        projectDestination, projectMetadata);

    File srcMainResourcesFileDestination = new File(projectDestination, SRC_MAIN_RESOURCES);
    File generatedFile =
        new File(srcMainResourcesFileDestination, APPLICATION_PROPERTIES_FILE_NAME);
    assertTrue(generatedFile.exists());

    String content = Files.readString(generatedFile.toPath());
    assertTrue(content.length() > 10);
  }

  @Test
  void testGenerateApplicationProperties_CreatesFileAndVerifiesContent() throws IOException {
    ProjectMetadata projectMetadata =
        new ProjectMetadata.ProjectMetadataBuilder().name("codegen-demo").build();

    File projectDestination = tempFolder.toFile();

    applicationPropertiesGenerator.generateApplicationProperties(
        projectDestination, projectMetadata);

    File srcMainResourcesFileDestination = new File(projectDestination, SRC_MAIN_RESOURCES);
    File generatedFile =
        new File(srcMainResourcesFileDestination, APPLICATION_PROPERTIES_FILE_NAME);
    assertTrue(generatedFile.exists());

    List<String> appPropertiesList = Files.readAllLines(generatedFile.toPath());
    assertThat(appPropertiesList, hasItem("spring.application.name=" + EXPECTED_APPLICATION_NAME));
  }

  @Test
  void testGenerateApplicationProperties_ThrowsExceptionOnTemplateEngineError() throws Exception {
    ProjectMetadata projectMetadata =
        new ProjectMetadata.ProjectMetadataBuilder().name("codegen-demo").build();

    File projectDestination = tempFolder.toFile();

    FreeMarkerTemplateEngine mockEngine = Mockito.mock(FreeMarkerTemplateEngine.class);
    SpringBootApplicationPropertiesGenerator generatorLocal =
        new SpringBootApplicationPropertiesGenerator(mockEngine);

    Mockito.doThrow(new IOException("Template processing error"))
        .when(mockEngine)
        .generateFileFromTemplate(any(), any(), any());

    assertThrows(
        IOException.class,
        () -> generatorLocal.generateApplicationProperties(projectDestination, projectMetadata));
  }
}
