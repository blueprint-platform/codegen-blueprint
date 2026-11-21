package io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.governance;

import io.github.blueprintplatform.codegen.adapter.error.exception.templating.ArchitectureGovernanceTemplatesNotFoundException;
import io.github.blueprintplatform.codegen.adapter.error.exception.templating.ArchitectureGovernanceTemplatesScanException;
import io.github.blueprintplatform.codegen.adapter.error.exception.templating.TemplateScanException;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.shared.templating.FtlClasspathTemplateScanner;
import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.GuardrailsMode;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ArchitectureGovernanceAdapter {

  private static final String PATH_SEPARATOR = "/";
  private static final String GOV_ROOT_DIR = "governance";

  private static final String SRC_TEST_JAVA = "src/test/java";
  private static final String JAVA_FTL_SUFFIX = ".java.ftl";
  private static final String FTL_SUFFIX = ".ftl";

  private static final String MODEL_KEY_PROJECT_PACKAGE_NAME = "projectPackageName";
  private static final String OUT_SEGMENT_ARCHITECTURE = "architecture";
  private static final String OUT_SEGMENT_ARCH_UNIT = "archunit";

  private static final String MVC_TEMPLATE_GROUP = "mvc/";

  private final TemplateRenderer renderer;
  private final ArtifactSpec artifactSpec;
  private final FtlClasspathTemplateScanner templateScanner;

  public ArchitectureGovernanceAdapter(
      TemplateRenderer renderer,
      ArtifactSpec artifactSpec,
      FtlClasspathTemplateScanner templateScanner) {
    this.renderer = renderer;
    this.artifactSpec = artifactSpec;
    this.templateScanner = templateScanner;
  }

  public Iterable<GeneratedResource> generate(ProjectBlueprint blueprint, boolean webSelected) {
    GuardrailsMode mode = blueprint.getArchitecture().governance().mode();
    if (!mode.isEnabled()) {
      return List.of();
    }

    ProjectLayout layout = blueprint.getArchitecture().layout();
    String templateRoot = resolveTemplateRoot(layout, mode);

    var templates =
        scanTemplates(templateRoot).stream()
            .filter(path -> path.endsWith(JAVA_FTL_SUFFIX))
            .map(path -> new GovernanceTemplate(path, path.substring(templateRoot.length() + 1)))
            .toList();
    if (templates.stream().noneMatch(template -> !template.requiresMvc())
        || mode == GuardrailsMode.STRICT
            && webSelected
            && templates.stream().noneMatch(GovernanceTemplate::requiresMvc)) {
      throw new ArchitectureGovernanceTemplatesNotFoundException(
          templateRoot, layout.key(), mode.key());
    }

    PackageName pkg = blueprint.getMetadata().packageName();
    String packagePath = pkg.value().replace('.', '/');

    Path outBase =
        Paths.get(SRC_TEST_JAVA)
            .resolve(packagePath)
            .resolve(OUT_SEGMENT_ARCHITECTURE)
            .resolve(OUT_SEGMENT_ARCH_UNIT);

    Map<String, Object> model = Map.of(MODEL_KEY_PROJECT_PACKAGE_NAME, pkg.value());

    List<GeneratedResource> generated = new ArrayList<>();
    templates.stream()
        .filter(template -> webSelected || !template.requiresMvc())
        .sorted(Comparator.comparing(GovernanceTemplate::outputName))
        .forEach(
            template ->
                generated.add(
                    renderer.renderUtf8(
                        outBase.resolve(template.outputName()), template.resourcePath(), model)));
    return List.copyOf(generated);
  }

  private record GovernanceTemplate(String resourcePath, String relativePath) {
    boolean requiresMvc() {
      return relativePath.startsWith(MVC_TEMPLATE_GROUP);
    }

    String outputName() {
      int start = requiresMvc() ? MVC_TEMPLATE_GROUP.length() : 0;
      return relativePath.substring(start, relativePath.length() - FTL_SUFFIX.length());
    }
  }

  private String resolveTemplateRoot(ProjectLayout layout, GuardrailsMode mode) {
    String basePath = artifactSpec.basePath();
    String normalizedBase =
        basePath.endsWith(PATH_SEPARATOR) ? basePath.substring(0, basePath.length() - 1) : basePath;

    return normalizedBase
        + PATH_SEPARATOR
        + GOV_ROOT_DIR
        + PATH_SEPARATOR
        + layout.key()
        + PATH_SEPARATOR
        + mode.key();
  }

  private List<String> scanTemplates(String templateRoot) {
    try {
      return templateScanner.scan(templateRoot);
    } catch (TemplateScanException e) {
      throw new ArchitectureGovernanceTemplatesScanException(templateRoot, e);
    }
  }
}
