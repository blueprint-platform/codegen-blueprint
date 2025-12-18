package io.github.blueprintplatform.codegen.adapter.out.templating;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.blueprintplatform.codegen.adapter.error.exception.TemplateRenderingException;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public class FreeMarkerTemplateRenderer implements TemplateRenderer {

  private final Configuration cfg;

  public FreeMarkerTemplateRenderer(Configuration cfg) {
    this.cfg = cfg;
  }

  @Override
  public GeneratedResource renderUtf8(
      Path outPath, String templateResourcePath, Map<String, Object> model) {
    try (StringWriter sw = new StringWriter()) {
      Template tpl = cfg.getTemplate(templateResourcePath);
      tpl.process(model, sw);
      return new GeneratedTextResource(outPath, sw.toString(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new TemplateRenderingException(templateResourcePath, e);
    }
  }
}
