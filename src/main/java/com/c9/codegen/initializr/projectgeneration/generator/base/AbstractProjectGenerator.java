package com.c9.codegen.initializr.projectgeneration.generator.base;

import com.c9.codegen.initializr.projectgeneration.generator.ProjectGenerator;
import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import com.c9.codegen.initializr.projectgeneration.ports.ApplicationPropertiesGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.FrameworkProjectStarterClassGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.FrameworkSpecificTestUnitGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.GitIgnoreGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectArchiver;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectBuildGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectDirectoryInitializer;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectDocumentationGenerator;
import com.c9.codegen.initializr.projectgeneration.ports.ProjectLayoutGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractProjectGenerator implements ProjectGenerator {

  private ProjectDirectoryInitializer projectDirectoryInitializer;
  private GitIgnoreGenerator gitIgnoreGenerator;
  private ProjectArchiver projectArchiver;
  private ProjectLayoutGenerator projectLayoutGenerator;
  private ProjectBuildGenerator projectBuildGenerator;
  private ApplicationPropertiesGenerator applicationPropertiesGenerator;
  private FrameworkProjectStarterClassGenerator frameworkProjectStarterClassGenerator;
  private FrameworkSpecificTestUnitGenerator frameworkSpecificTestUnitGenerator;
  private ProjectDocumentationGenerator projectDocumentationGenerator;

  protected AbstractProjectGenerator(
      ProjectDirectoryInitializer projectDirectoryInitializer,
      GitIgnoreGenerator gitIgnoreGenerator,
      ProjectArchiver projectArchiver,
      ProjectLayoutGenerator projectLayoutGenerator,
      ProjectBuildGenerator projectBuildGenerator,
      ApplicationPropertiesGenerator applicationPropertiesGenerator,
      FrameworkProjectStarterClassGenerator frameworkProjectStarterClassGenerator,
      FrameworkSpecificTestUnitGenerator frameworkSpecificTestUnitGenerator,
      ProjectDocumentationGenerator projectDocumentationGenerator) {
    this.projectDirectoryInitializer = projectDirectoryInitializer;
    this.gitIgnoreGenerator = gitIgnoreGenerator;
    this.projectArchiver = projectArchiver;
    this.projectLayoutGenerator = projectLayoutGenerator;
    this.projectBuildGenerator = projectBuildGenerator;
    this.applicationPropertiesGenerator = applicationPropertiesGenerator;
    this.frameworkProjectStarterClassGenerator = frameworkProjectStarterClassGenerator;
    this.frameworkSpecificTestUnitGenerator = frameworkSpecificTestUnitGenerator;
    this.projectDocumentationGenerator = projectDocumentationGenerator;
  }

  @Override
  public final Path generateProject(ProjectMetadata projectMetadata) throws IOException {
    Path projectDestinationPath = initializeProjectDirectory(projectMetadata);
    File projectDestination = projectDestinationPath.toFile();

    generateGitIgnoreContent(projectDestination);
    generateProjectLayout(projectDestination, projectMetadata);
    generateBuildConfiguration(projectDestination, projectMetadata);
    generateApplicationProperties(projectDestination, projectMetadata);
    generateProjectStarterClass(projectDestination, projectMetadata);
    generateTestClass(projectDestination, projectMetadata);
    generateProjectDocument(projectDestination, projectMetadata);
    return archiveProject(projectDestination, projectMetadata);
  }

  protected Path initializeProjectDirectory(ProjectMetadata projectMetadata) throws IOException {
    return projectDirectoryInitializer.initializeProjectDirectory(projectMetadata.getArtifactId());
  }

  protected void generateGitIgnoreContent(File projectDestination) throws IOException {
    List<String> ignoreList = Collections.emptyList();
    gitIgnoreGenerator.generateGitIgnoreContent(projectDestination, Optional.of(ignoreList));
  }

  protected void generateProjectLayout(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException {
    projectLayoutGenerator.generateProjectLayout(projectDestination, projectMetadata);
  }

  protected void generateBuildConfiguration(
      File projectDestination, ProjectMetadata projectMetadata) throws IOException {
    projectBuildGenerator.generateBuildConfiguration(projectDestination, projectMetadata);
  }

  protected void generateApplicationProperties(
      File projectDestination, ProjectMetadata projectMetadata) throws IOException {
    applicationPropertiesGenerator.generateApplicationProperties(
        projectDestination, projectMetadata);
  }

  protected void generateProjectStarterClass(
      File projectDestination, ProjectMetadata projectMetadata) throws IOException {
    frameworkProjectStarterClassGenerator.generateProjectStarterClass(
        projectDestination, projectMetadata);
  }

  protected void generateTestClass(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException {
    frameworkSpecificTestUnitGenerator.generateTestClass(projectDestination, projectMetadata);
  }

  protected void generateProjectDocument(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException {
    projectDocumentationGenerator.generateProjectDocument(projectDestination, projectMetadata);
  }

  protected Path archiveProject(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException {
    return projectArchiver.archiveProject(projectDestination, projectMetadata.getName());
  }
}
