package io.github.blueprintplatform.codegen.testsupport.templating;

import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RecordingTemplateRenderer implements TemplateRenderer {

  public final List<Path> capturedOutPaths = new ArrayList<>();
  public final List<String> capturedTemplateNames = new ArrayList<>();
  public final List<Map<String, Object>> capturedModels = new ArrayList<>();
  public final List<GeneratedResource> renderedFiles = new ArrayList<>();

  private final List<GeneratedResource> filesToReturn;

  public RecordingTemplateRenderer() {
    this(List.of());
  }

  public RecordingTemplateRenderer(List<GeneratedResource> filesToReturn) {
    this.filesToReturn = (filesToReturn == null) ? List.of() : List.copyOf(filesToReturn);
  }

  @Override
  public GeneratedResource renderUtf8(
      Path outPath, String templateResourcePath, Map<String, Object> model) {

    capturedOutPaths.add(outPath);
    capturedTemplateNames.add(templateResourcePath);
    capturedModels.add(model);

    GeneratedResource next = nextResource(outPath);
    renderedFiles.add(next);
    return next;
  }

  private GeneratedResource nextResource(Path outPath) {
    int idx = renderedFiles.size();
    if (idx < filesToReturn.size()) {
      return filesToReturn.get(idx);
    }
    return new GeneratedTextResource(outPath, "", StandardCharsets.UTF_8);
  }
}
