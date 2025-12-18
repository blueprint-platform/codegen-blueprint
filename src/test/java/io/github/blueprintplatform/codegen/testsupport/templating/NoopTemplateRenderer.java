package io.github.blueprintplatform.codegen.testsupport.templating;

import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public final class NoopTemplateRenderer implements TemplateRenderer {

  @Override
  public GeneratedResource renderUtf8(
      Path outPath, String templateResourcePath, Map<String, Object> model) {
    return new GeneratedTextResource(outPath, "", StandardCharsets.UTF_8);
  }
}
