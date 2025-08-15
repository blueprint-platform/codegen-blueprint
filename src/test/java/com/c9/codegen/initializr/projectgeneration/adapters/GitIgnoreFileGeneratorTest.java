package com.c9.codegen.initializr.projectgeneration.adapters;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.c9.codegen.initializr.projectgeneration.adapters.templating.FreeMarkerTemplateEngine;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GitIgnoreFileGeneratorTest {

  private static final String GITIGNORE_FILE_NAME = ".gitignore";

  @Autowired private GitIgnoreFileGenerator gitIgnoreFileGenerator;

  @TempDir private Path tempFolder;

  @Test
  void testGenerateGitIgnoreContent_CreatesCorrectFileWithEmptyIgnoreList() throws IOException {
    Optional<List<String>> emptyList = Optional.empty();
    gitIgnoreFileGenerator.generateGitIgnoreContent(tempFolder.toFile(), emptyList);

    Path generatedFile = tempFolder.resolve(GITIGNORE_FILE_NAME);
    assertTrue(Files.exists(generatedFile));
    String content = Files.readString(generatedFile);
    assertTrue(content.length() > 10);
  }

  @Test
  void testGenerateGitIgnoreContent_CreatesCorrectFileWithSomeIgnoreList() throws IOException {
    List<String> ignoreList = Arrays.asList("*.pyc", "__pycache__");
    Optional<List<String>> optionalIgnoreList = Optional.of(ignoreList);
    gitIgnoreFileGenerator.generateGitIgnoreContent(tempFolder.toFile(), optionalIgnoreList);

    Path generatedFile = tempFolder.resolve(GITIGNORE_FILE_NAME);
    assertTrue(Files.exists(generatedFile));
    String content = Files.readString(generatedFile);
    assertTrue(content.contains("*.py"));
    assertTrue(content.contains("__pycache__"));
  }

  @Test
  void testGenerateGitIgnoreContent_CreatesFileAndVerifiesContent() throws IOException {
    File projectDestination = tempFolder.toFile();

    List<String> additionalIgnorePatterns = List.of("*.md");
    Optional<List<String>> optionalIgnoreList = Optional.of(additionalIgnorePatterns);

    gitIgnoreFileGenerator.generateGitIgnoreContent(projectDestination, optionalIgnoreList);

    File generatedFile = new File(projectDestination, GITIGNORE_FILE_NAME);
    assertTrue(generatedFile.exists(), "Generated file should be created!");

    List<String> gitIgnoreList =
        Files.readAllLines(generatedFile.toPath()).stream()
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();

    assertThat(gitIgnoreList, hasItems("*.com", "*.md", ".idea/", "target/", "generated-sources/"));
  }

  @Test
  void testGenerateGitIgnoreContent_ThrowsExceptionOnTemplateEngineError() throws Exception {
    Optional<List<String>> emptyList = Optional.empty();

    FreeMarkerTemplateEngine mockEngine = Mockito.mock(FreeMarkerTemplateEngine.class);
    GitIgnoreFileGenerator gitIgnoreFileGeneratorLocal = new GitIgnoreFileGenerator(mockEngine);

    Mockito.doThrow(new IOException("Template processing error"))
        .when(mockEngine)
        .generateFileFromTemplate(any(), any(), any());

    assertThrows(
        IOException.class,
        () -> gitIgnoreFileGeneratorLocal.generateGitIgnoreContent(tempFolder.toFile(), emptyList));
  }
}
