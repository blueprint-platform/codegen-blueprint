package io.github.blueprintplatform.codegen.testsupport.templating;

import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.util.Map;

public final class CapturingTemplateRenderer implements TemplateRenderer {

  public Path capturedOutPath;
  public String capturedTemplateName;
  public Map<String, Object> capturedModel;
  public GeneratedResource nextFile;

  @Override
  public GeneratedResource renderUtf8(
      Path outPath, String templateName, Map<String, Object> model) {
    this.capturedOutPath = outPath;
    this.capturedTemplateName = templateName;
    this.capturedModel = model;
    return nextFile;
  }
}
