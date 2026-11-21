package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.github.blueprintplatform.codegen.adapter.error.exception.artifact.ArtifactBindingException;
import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependencyMapper;
import io.github.blueprintplatform.codegen.adapter.out.framework.springboot.java.governance.ArchitectureGovernanceAdapter;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenDependencyPlan;
import io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build.MavenPomBuildConfigurationAdapter;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactPort;
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
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
            ProjectLayout.STANDARD, ArchitectureGovernance.none(), SampleCodeOptions.basic()),
        Dependencies.of(List.of()));
  }

  @Test
  void sharesImplicitMvcDecisionWithPomAndGovernanceInConfiguredOrder() {
    var blueprint = blueprint();
    var pom = mock(MavenPomBuildConfigurationAdapter.class);
    var governance = mock(ArchitectureGovernanceAdapter.class);
    var dependencies = mock(BuildDependencyMapper.class);
    var standalone = mock(ArtifactPort.class);
    when(dependencies.from(blueprint.getDependencies())).thenReturn(List.of());
    when(standalone.artifactKey()).thenReturn(ArtifactKey.SOURCE_LAYOUT);
    var plan = ArgumentCaptor.forClass(MavenDependencyPlan.class);
    when(pom.generate(eq(blueprint), any())).thenReturn(List.of(resource("pom")));
    when(governance.generate(blueprint, true)).thenReturn(List.of(resource("rules")));
    when(standalone.generate(blueprint)).thenReturn(List.of(resource("layout")));
    var adapter =
        new SpringBootMavenJavaArtifactsAdapter(
            pom,
            governance,
            dependencies,
            List.of(
                ArtifactKey.ARCHITECTURE_GOVERNANCE,
                ArtifactKey.SOURCE_LAYOUT,
                ArtifactKey.BUILD_CONFIG),
            List.of(standalone));

    assertThat(adapter.generate(blueprint))
        .extracting(GeneratedResource::relativePath)
        .containsExactly(Path.of("rules"), Path.of("layout"), Path.of("pom"));
    verify(dependencies).from(blueprint.getDependencies());
    var execution = inOrder(governance, standalone, pom);
    execution.verify(governance).generate(blueprint, true);
    execution.verify(standalone).generate(blueprint);
    execution.verify(pom).generate(eq(blueprint), plan.capture());
    assertThat(plan.getValue().mvcPresent()).isTrue();
    assertThat(plan.getValue().dependencies())
        .anyMatch(d -> d.artifactId().equals("spring-boot-starter-webmvc"));
  }

  @Test
  void rejectsDuplicateAndMissingArtifactBindings() {
    var pom = mock(MavenPomBuildConfigurationAdapter.class);
    var governance = mock(ArchitectureGovernanceAdapter.class);
    var dependencies = mock(BuildDependencyMapper.class);
    var layout = mock(ArtifactPort.class);
    when(layout.artifactKey()).thenReturn(ArtifactKey.SOURCE_LAYOUT);
    var order = List.of(ArtifactKey.SOURCE_LAYOUT);

    assertThatThrownBy(
            () ->
                new SpringBootMavenJavaArtifactsAdapter(
                    pom, governance, dependencies, order, List.of(layout, layout)))
        .isInstanceOf(ArtifactBindingException.class)
        .hasMessage("adapter.artifact.binding.duplicate");
    assertThatThrownBy(
            () ->
                new SpringBootMavenJavaArtifactsAdapter(
                    pom, governance, dependencies, order, List.of()))
        .isInstanceOf(ArtifactBindingException.class)
        .hasMessage("adapter.artifact.binding.missing");
  }

  private static GeneratedDirectory resource(String name) {
    return new GeneratedDirectory(Path.of(name));
  }
}
