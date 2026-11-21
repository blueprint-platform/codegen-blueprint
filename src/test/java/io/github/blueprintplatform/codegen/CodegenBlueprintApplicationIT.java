package io.github.blueprintplatform.codegen;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
@DisplayName("Integration Test: Spring Context Bootstrapping")
class CodegenBlueprintApplicationIT {

  @Test
  @DisplayName("Spring context should load successfully")
  void contextLoads() {
    // If context fails to load, the test will fail automatically.
  }

  @Test
  void startupRejectsConfiguredTemplateThatDoesNotExist() {
    String buildTemplate =
        "--codegen.profiles.spring-boot-maven-java.artifacts.build-config.templates[0].";

    assertThatThrownBy(
            () ->
                new SpringApplicationBuilder(CodegenBlueprintApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(buildTemplate + "template=missing-pom.ftl", buildTemplate + "output-path=pom.xml"))
        .hasStackTraceContaining("bootstrap.artifact.template.unavailable")
        .hasStackTraceContaining("missing-pom.ftl");
  }
}
