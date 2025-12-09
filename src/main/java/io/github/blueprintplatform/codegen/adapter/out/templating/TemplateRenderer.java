package io.github.blueprintplatform.codegen.adapter.out.templating;

import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.util.Map;

public interface TemplateRenderer {
  GeneratedResource renderUtf8(Path outPath, String templateName, Map<String, Object> model);
}
