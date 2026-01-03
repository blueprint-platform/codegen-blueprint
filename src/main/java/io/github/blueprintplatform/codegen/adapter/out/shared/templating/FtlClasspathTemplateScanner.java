package io.github.blueprintplatform.codegen.adapter.out.shared.templating;

import io.github.blueprintplatform.codegen.adapter.error.exception.templating.TemplateScanException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

public class FtlClasspathTemplateScanner {

  private static final String TEMPLATES_ROOT_DIR = "templates";
  private static final String PATH_SEPARATOR = "/";
  private static final String FTL_SUFFIX = ".ftl";

  private final ResourcePatternResolver resolver;

  public FtlClasspathTemplateScanner(ResourcePatternResolver resolver) {
    this.resolver = resolver;
  }

  public List<String> scan(String templateRoot) {
    String normalizedRoot = normalizeRoot(templateRoot);
    if (normalizedRoot.isBlank()) {
      return List.of();
    }

    String pattern =
        "classpath*:"
            + PATH_SEPARATOR
            + TEMPLATES_ROOT_DIR
            + PATH_SEPARATOR
            + normalizedRoot
            + PATH_SEPARATOR
            + "**"
            + PATH_SEPARATOR
            + "*"
            + FTL_SUFFIX;

    try {
      Resource[] resources = resolver.getResources(pattern);
      if (resources.length == 0) {
        return List.of();
      }

      String marker =
          PATH_SEPARATOR + TEMPLATES_ROOT_DIR + PATH_SEPARATOR + normalizedRoot + PATH_SEPARATOR;

      List<String> result = new ArrayList<>(resources.length);

      for (Resource resource : resources) {
        String decoded = URLDecoder.decode(resource.getURL().toString(), StandardCharsets.UTF_8);

        String relative = extractRelative(decoded, marker);
        if (relative == null || relative.isBlank()) {
          continue;
        }

        String normalizedRelative = relative.replace('\\', '/');
        boolean isFtl = normalizedRelative.endsWith(FTL_SUFFIX);

        if (isFtl) {
          result.add(normalizedRoot + PATH_SEPARATOR + normalizedRelative);
        }
      }

      return result.stream().distinct().sorted(Comparator.naturalOrder()).toList();

    } catch (IOException e) {
      throw new TemplateScanException(templateRoot, e);
    }
  }

  private String normalizeRoot(String templateRoot) {
    if (templateRoot == null || templateRoot.isBlank()) {
      return "";
    }
    String v = templateRoot.replace('\\', '/');
    while (v.startsWith(PATH_SEPARATOR)) {
      v = v.substring(1);
    }
    while (v.endsWith(PATH_SEPARATOR)) {
      v = v.substring(0, v.length() - 1);
    }
    return v;
  }

  private String extractRelative(String decodedUrl, String marker) {
    int idx = decodedUrl.indexOf(marker);
    if (idx < 0) {
      return null;
    }
    return decodedUrl.substring(idx + marker.length());
  }
}
