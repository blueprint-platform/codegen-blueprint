package io.github.bsayli.codegen.initializr.bootstrap.wiring;

import io.github.bsayli.codegen.initializr.adapter.artifact.ArtifactKey;
import io.github.bsayli.codegen.initializr.adapter.error.exception.ArtifactKeyMismatchException;
import io.github.bsayli.codegen.initializr.adapter.error.exception.GeneratorFactoryNotFoundException;
import io.github.bsayli.codegen.initializr.adapter.out.SpringBootMavenJavaArtifactsAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.build.MavenPomAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.config.ApplicationYamlAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.docs.ReadmeAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.profile.springboot.maven.java.vcs.GitIgnoreAdapter;
import io.github.bsayli.codegen.initializr.adapter.out.templating.TemplateRenderer;
import io.github.bsayli.codegen.initializr.adapter.profile.ProfileType;
import io.github.bsayli.codegen.initializr.application.port.out.ProjectArtifactsPort;
import io.github.bsayli.codegen.initializr.application.port.out.artifacts.ArtifactPort;
import io.github.bsayli.codegen.initializr.bootstrap.config.ArtifactProperties;
import io.github.bsayli.codegen.initializr.bootstrap.config.CodegenProfilesProperties;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.context.annotation.Bean;

// @Configuration
class ArtifactsWiring {

    private static final Map<ArtifactKey, ArtifactFactory> FACTORIES = Map.of(
            ArtifactKey.POM, MavenPomAdapter::new,
            ArtifactKey.GITIGNORE, GitIgnoreAdapter::new,
            ArtifactKey.APPLICATION_YAML, ApplicationYamlAdapter::new,
            ArtifactKey.README, ReadmeAdapter::new
    );

    @Bean
    ProjectArtifactsPort springBootMavenJavaArtifactsAdapter(
            TemplateRenderer renderer, CodegenProfilesProperties profiles) {

        var type = ProfileType.SPRINGBOOT_MAVEN_JAVA;
        var profile = profiles.requireProfile(type);

        var artifacts = profile.run().stream()
                .map(expectedKey -> {
                    var f = FACTORIES.get(expectedKey);
                    if (f == null) throw new GeneratorFactoryNotFoundException(expectedKey);
                    var props = profiles.artifact(type, expectedKey);
                    var port = f.apply(renderer, props);
                    if (!port.artifactKey().equals(expectedKey))
                        throw new ArtifactKeyMismatchException(expectedKey, port.artifactKey());
                    return port;
                })
                .toList();

        return new SpringBootMavenJavaArtifactsAdapter(artifacts);
    }

    @FunctionalInterface
    interface ArtifactFactory
            extends BiFunction<TemplateRenderer, ArtifactProperties, ArtifactPort> {}
}