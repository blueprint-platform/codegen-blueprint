package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependency;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenPomBuildConfigurationAdapter;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureGovernance;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedDirectory;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class SpringBootMavenJavaArtifactsAdapterTest {

  private static ProjectBlueprint blueprint() {
    return ProjectBlueprint.of(
        new ProjectMetadata(
            new ProjectIdentity(
                new GroupId("io.github.blueprintplatform"), new ArtifactId("greeting")),
            new ProjectName("Greeting"),
            new ProjectDescription("Greeting sample built with hexagonal architecture"),
            new PackageName("io.github.blueprintplatform.greeting")),
        new PlatformSpec(
            new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA),
            new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0")),
        new ArchitectureSpec(
            ProjectLayout.STANDARD, ArchitectureGovernance.none(), SampleCodeOptions.none()),
        Dependencies.of(List.of()));
  }

  @Test
  void computesOnePlanAndSharesItWithOrderedSteps() {
    var blueprint = blueprint();
    var pom = mock(MavenPomBuildConfigurationAdapter.class);
    var dependency = BuildDependency.of("org.springframework.boot", "spring-boot-starter-webmvc");
    when(pom.effectiveDependencies(blueprint)).thenReturn(List.of(dependency));
    var plans = new ArrayList<SpringBootMavenJavaArtifactsAdapter.EffectiveDependencies>();
    var first = new GeneratedDirectory(Path.of("first"));
    var second = new GeneratedDirectory(Path.of("second"));
    var adapter =
        new SpringBootMavenJavaArtifactsAdapter(
            pom,
            List.of(
                (bp, plan) -> {
                  plans.add(plan);
                  return List.of(first);
                },
                (bp, plan) -> {
                  plans.add(plan);
                  return List.of(second);
                }));

    assertThat(adapter.generate(blueprint))
        .extracting(GeneratedResource::relativePath)
        .containsExactly(first.relativePath(), second.relativePath());
    assertThat(plans.getFirst().dependencies()).containsExactly(dependency);
    assertThat(plans.getFirst().mvcPresent()).isTrue();
    assertThat(plans.getLast()).isSameAs(plans.getFirst());
    verify(pom).effectiveDependencies(blueprint);
  }
}
