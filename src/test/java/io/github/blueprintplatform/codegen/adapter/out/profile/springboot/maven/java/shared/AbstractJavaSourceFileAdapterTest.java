package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.shared;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.TemplateSpec;
import io.github.blueprintplatform.codegen.adapter.out.templating.TemplateRenderer;
import io.github.blueprintplatform.codegen.adapter.shared.naming.StringCaseFormatter;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import io.github.blueprintplatform.codegen.testsupport.templating.CapturingTemplateRenderer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("adapter")
class AbstractJavaSourceFileAdapterTest {

  private static final String BASE_PATH = "springboot/maven/java/";

  @Test
  @DisplayName("generate() should build correct path, model and return single file")
  void generate_shouldBuildOutPathAndModelAndReturnFile() {
    CapturingTemplateRenderer renderer = new CapturingTemplateRenderer();

    TemplateSpec templateSpec = new TemplateSpec("java-class.ftl", "src/main/java");

    ArtifactSpec artifactSpec = new ArtifactSpec(BASE_PATH, List.of(templateSpec));

    StringCaseFormatter formatter = new StringCaseFormatter();

    TestJavaSourceFileAdapter adapter =
        new TestJavaSourceFileAdapter(renderer, artifactSpec, formatter);

    ProjectBlueprint blueprint =
        new ProjectBlueprint(
            null, null, null, new PackageName("com.acme.demo"), null, null, null, null, null);

    Path expectedPath = Path.of("src/main/java/com/acme/demo/DemoApplication.java");

    GeneratedTextResource expectedFile =
        new GeneratedTextResource(expectedPath, "class DemoApplication {}", StandardCharsets.UTF_8);
    renderer.nextFile = expectedFile;

    Iterable<? extends GeneratedResource> result = adapter.generate(blueprint);

    assertThat(result).singleElement().isSameAs(expectedFile);

    assertThat(renderer.capturedOutPath).isEqualTo(expectedPath);
    assertThat(renderer.capturedTemplateName).isEqualTo(BASE_PATH + "java-class.ftl");

    assertThat(renderer.capturedModel)
        .isNotNull()
        .containsEntry("projectPackageName", "com.acme.demo")
        .containsEntry("className", "DemoApplication");
  }

  private static final class TestJavaSourceFileAdapter extends AbstractJavaSourceFileAdapter {

    TestJavaSourceFileAdapter(
        TemplateRenderer renderer,
        ArtifactSpec artifactSpec,
        StringCaseFormatter stringCaseFormatter) {
      super(renderer, artifactSpec, stringCaseFormatter);
    }

    @Override
    protected String buildClassName(ProjectBlueprint blueprint) {
      return "DemoApplication";
    }

    @Override
    public ArtifactKey artifactKey() {
      return ArtifactKey.MAIN_SOURCE_ENTRY_POINT;
    }
  }
}
