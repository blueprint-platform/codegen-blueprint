package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.bootstrap.config.ArtifactDefinition;
import io.github.blueprintplatform.codegen.bootstrap.config.TemplateDefinition;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
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
class ApplicationYamlAdapterTest {

  private static final String BASE_PATH = "springboot/maven/java/";

  private static ProjectBlueprint blueprint() {
    ProjectIdentity identity =
        new ProjectIdentity(new GroupId("com.acme"), new ArtifactId("demo-app"));

    ProjectName name = new ProjectName("Demo App");
    ProjectDescription description = new ProjectDescription("Sample Project");
    PackageName pkg = new PackageName("com.acme.demo");

    TechStack techStack = new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);
    ProjectLayout layout = ProjectLayout.STANDARD;
    PlatformTarget platformTarget =
        new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V3_5);
    Dependencies dependencies = Dependencies.of(List.of());

    SampleCodeOptions sampleCodeOptions = SampleCodeOptions.none();

    return new ProjectBlueprint(
        identity,
        name,
        description,
        pkg,
        techStack,
        layout,
        platformTarget,
        dependencies,
        sampleCodeOptions);
  }

  @Test
  @DisplayName("artifactKey() should return APP_CONFIG")
  void artifactKey_shouldReturnApplicationYaml() {
    ApplicationYamlAdapter adapter =
        new ApplicationYamlAdapter(
            new NoopTemplateRenderer(),
            new ArtifactDefinition(
                BASE_PATH,
                List.of(new TemplateDefinition("application-yaml.ftl", "application.yml"))));

    assertThat(adapter.artifactKey()).isEqualTo(ArtifactKey.APP_CONFIG);
  }

  @Test
  @DisplayName(
      "generate() should build model with applicationName from artifactId and render single file")
  void generate_shouldBuildModelAndRenderFile() {
    CapturingTemplateRenderer renderer = new CapturingTemplateRenderer();

    TemplateDefinition templateDefinition =
        new TemplateDefinition("application-yaml.ftl", "src/main/resources/application.yml");
    ArtifactDefinition artifactDefinition =
        new ArtifactDefinition(BASE_PATH, List.of(templateDefinition));

    ApplicationYamlAdapter adapter = new ApplicationYamlAdapter(renderer, artifactDefinition);

    ProjectBlueprint blueprint = blueprint();

    Path relativePath = Path.of("src/main/resources/application.yml");
    GeneratedTextResource expectedFile =
        new GeneratedTextResource(
            relativePath, "spring:\n  application:\n    name: demo-app\n", StandardCharsets.UTF_8);

    renderer.nextFile = expectedFile;

    Iterable<? extends GeneratedResource> result = adapter.generate(blueprint);

    assertThat(result).singleElement().isSameAs(expectedFile);

    assertThat(renderer.capturedOutPath).isEqualTo(relativePath);
    assertThat(renderer.capturedTemplateName).isEqualTo(BASE_PATH + "application-yaml.ftl");

    Map<String, Object> model = renderer.capturedModel;
    assertThat(model).isNotNull().containsEntry("applicationName", "demo-app");
  }
}
