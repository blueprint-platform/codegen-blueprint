package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.ArtifactSpec;
import io.github.blueprintplatform.codegen.adapter.out.shared.artifact.TemplateSpec;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import io.github.blueprintplatform.codegen.testsupport.templating.CapturingTemplateRenderer;
import io.github.blueprintplatform.codegen.testsupport.templating.NoopTemplateRenderer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("adapter")
class GitIgnoreAdapterTest {

  private static final String BASE_PATH = "springboot/maven/java/";

  @Test
  @DisplayName("artifactKey() should return IGNORE_RULES")
  void artifactKey_shouldReturnIgnoreRules() {
    GitIgnoreAdapter adapter =
        new GitIgnoreAdapter(
            new NoopTemplateRenderer(),
            new ArtifactSpec(BASE_PATH, List.of(new TemplateSpec("gitignore.ftl", ".gitignore"))));

    assertThat(adapter.artifactKey()).isEqualTo(ArtifactKey.IGNORE_RULES);
  }

  @Test
  @DisplayName("generate() should render ignore rules with an empty ignoreList model")
  void generate_shouldRenderGitignoreWithEmptyIgnoreList() {
    CapturingTemplateRenderer renderer = new CapturingTemplateRenderer();

    TemplateSpec templateSpec = new TemplateSpec("gitignore.ftl", ".gitignore");
    ArtifactSpec artifactSpec = new ArtifactSpec(BASE_PATH, List.of(templateSpec));

    GitIgnoreAdapter adapter = new GitIgnoreAdapter(renderer, artifactSpec);

    ProjectBlueprint blueprint =
        new ProjectBlueprint(null, null, null, null, null, null, null, null, null);

    Path relativePath = Path.of(".gitignore");
    GeneratedTextResource expectedFile =
        new GeneratedTextResource(relativePath, "# gitignore", StandardCharsets.UTF_8);
    renderer.nextFile = expectedFile;

    Iterable<? extends GeneratedResource> result = adapter.generate(blueprint);

    assertThat(result).singleElement().isSameAs(expectedFile);

    assertThat(renderer.capturedOutPath).isEqualTo(relativePath);
    assertThat(renderer.capturedTemplateName).isEqualTo(BASE_PATH + "gitignore.ftl");

    Map<String, Object> model = renderer.capturedModel;
    assertThat(model).isNotNull().containsEntry("ignoreList", List.of());
  }
}
