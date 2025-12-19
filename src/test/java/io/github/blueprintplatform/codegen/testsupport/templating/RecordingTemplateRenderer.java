package io.github.blueprintplatform.codegen.testsupport.templating;

import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
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

  public RecordingTemplateRenderer(List<GeneratedResource> filesToReturn) {
    this.filesToReturn = filesToReturn;
  }

  @Override
  public GeneratedResource renderUtf8(
      Path outPath, String templateResourcePath, Map<String, Object> model) {

    capturedOutPaths.add(outPath);
    capturedTemplateNames.add(templateResourcePath);
    capturedModels.add(model);

    GeneratedResource next = filesToReturn.get(renderedFiles.size());
    renderedFiles.add(next);
    return next;
  }
}
