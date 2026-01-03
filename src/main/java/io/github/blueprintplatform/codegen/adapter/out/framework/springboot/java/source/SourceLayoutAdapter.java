package io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.source;

import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.application.port.out.artifact.SourceLayoutPort;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SourceLayoutAdapter implements SourceLayoutPort {

  private static final Path SRC_MAIN_JAVA = Path.of("src", "main", "java");
  private static final Path SRC_TEST_JAVA = Path.of("src", "test", "java");
  private static final Path SRC_MAIN_RESOURCES = Path.of("src", "main", "resources");
  private static final Path SRC_TEST_RESOURCES = Path.of("src", "test", "resources");

  private static final List<Path> COMMON_DIRS =
      List.of(SRC_MAIN_JAVA, SRC_TEST_JAVA, SRC_MAIN_RESOURCES, SRC_TEST_RESOURCES);

  private static final List<Path> HEX_DIRS =
      List.of(
          Path.of("adapter"),
          Path.of("adapter", "in"),
          Path.of("adapter", "out"),
          Path.of("application"),
          Path.of("application", "port"),
          Path.of("application", "port", "in"),
          Path.of("application", "port", "out"),
          Path.of("application", "usecase"),
          Path.of("bootstrap"),
          Path.of("domain"),
          Path.of("domain", "model"),
          Path.of("domain", "port"),
          Path.of("domain", "port", "in"),
          Path.of("domain", "port", "out"),
          Path.of("domain", "service"));

  private static final List<Path> STANDARD_DIRS =
      List.of(
          Path.of("controller"),
          Path.of("controller", "dto"),
          Path.of("service"),
          Path.of("repository"),
          Path.of("config"),
          Path.of("domain"),
          Path.of("domain", "model"),
          Path.of("domain", "service"));

  private static final List<String> HEX_TOP_LEVEL_FAMILIES =
      List.of("adapter", "application", "domain", "bootstrap");

  private static final List<String> STANDARD_TOP_LEVEL_FAMILIES =
      List.of("controller", "service", "repository", "domain", "config");

  private static final String PACKAGE_INFO_FILE = "package-info.java";

  @Override
  public ArtifactKey artifactKey() {
    return ArtifactKey.SOURCE_LAYOUT;
  }

  @Override
  public Iterable<? extends GeneratedResource> generate(ProjectBlueprint blueprint) {
    var packageName = blueprint.getMetadata().packageName();

    var mainBasePackageDir = resolveBasePackageDir(SRC_MAIN_JAVA, packageName);
    var testBasePackageDir = resolveBasePackageDir(SRC_TEST_JAVA, packageName);

    var resources = new ArrayList<GeneratedResource>();
    addDirectories(resources, COMMON_DIRS);
    addDirectories(resources, List.of(mainBasePackageDir, testBasePackageDir));

    var isHex = blueprint.getArchitecture().layout().isHexagonal();

    var segments = isHex ? HEX_DIRS : STANDARD_DIRS;
    addDirectoriesUnder(resources, mainBasePackageDir, segments);

    addTopLevelPackageInfoMarkers(resources, mainBasePackageDir, packageName, isHex);

    return List.copyOf(resources);
  }

  private void addTopLevelPackageInfoMarkers(
      List<GeneratedResource> out,
      Path mainBasePackageDir,
      PackageName basePackage,
      boolean isHex) {
    var families = isHex ? HEX_TOP_LEVEL_FAMILIES : STANDARD_TOP_LEVEL_FAMILIES;
    for (var family : families) {
      var relativePath = mainBasePackageDir.resolve(family).resolve(PACKAGE_INFO_FILE);
      var content = "package " + basePackage.value() + "." + family + ";\n";
      out.add(new GeneratedTextResource(relativePath, content, StandardCharsets.UTF_8));
    }
  }

  private Path resolveBasePackageDir(Path javaRoot, PackageName packageName) {
    var pkgPath = Path.of(packageName.value().replace('.', '/'));
    return javaRoot.resolve(pkgPath);
  }

  private void addDirectories(List<GeneratedResource> out, List<Path> dirs) {
    for (var dir : dirs) {
      out.add(new GeneratedDirectory(dir));
    }
  }

  private void addDirectoriesUnder(List<GeneratedResource> out, Path base, List<Path> segments) {
    for (var segment : segments) {
      out.add(new GeneratedDirectory(base.resolve(segment)));
    }
  }
}
