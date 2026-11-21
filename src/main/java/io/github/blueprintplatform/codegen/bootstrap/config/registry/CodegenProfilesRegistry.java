package io.github.blueprintplatform.codegen.bootstrap.config.registry;

import io.github.blueprintplatform.codegen.application.error.exception.InvalidArtifactKeyException;
import io.github.blueprintplatform.codegen.application.port.out.artifact.ArtifactKey;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.ArtifactDefinition;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.CodegenProfilesProperties;
import io.github.blueprintplatform.codegen.bootstrap.config.properties.ProfileProperties;
import io.github.blueprintplatform.codegen.bootstrap.error.exception.ProfileConfigurationException;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.policy.file.GeneratedFilePolicy;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CodegenProfilesRegistry {

  private static final Set<String> WRAPPER_OUTPUTS =
      Set.of("mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties");

  private final CodegenProfilesProperties properties;

  public CodegenProfilesRegistry(CodegenProfilesProperties properties) {
    this.properties = properties;
  }

  public ProfileProperties requireProfile(String profileKey) {
    var profileProps = properties.profiles().get(profileKey);
    if (profileProps == null) {
      throw new ProfileConfigurationException(
          ProfileConfigurationException.KEY_PROFILE_NOT_FOUND, profileKey);
    }
    return profileProps;
  }

  public ProfileProperties requireValidatedProfile(String profileKey) {
    var profile = requireProfile(profileKey);
    var ordered = validateArtifactOrder(profileKey, profile.orderedArtifactKeys());

    Set<Path> outputs = new HashSet<>();
    for (var entry : profile.artifacts().entrySet()) {
      ArtifactKey key;
      try {
        key = ArtifactKey.fromKey(entry.getKey());
      } catch (InvalidArtifactKeyException failure) {
        throw new ProfileConfigurationException(
            "bootstrap.profile.artifact.unknown", failure, profileKey, entry.getKey());
      }
      var artifact = entry.getValue();
      validateArtifact(profileKey, key, artifact);
      // Entrypoints declare directories, and can share them without writing the same file.
      if (ordered.contains(key)
          && key != ArtifactKey.MAIN_SOURCE_ENTRY_POINT
          && key != ArtifactKey.TEST_SOURCE_ENTRY_POINT) {
        for (var template : artifact.templates()) {
          if (!outputs.add(Path.of(template.outputPath()))) {
            throw new ProfileConfigurationException(
                "bootstrap.profile.output.duplicate", profileKey, template.outputPath());
          }
        }
      }
    }
    validateOutputParents(profileKey, outputs);
    return profile;
  }

  private Set<ArtifactKey> validateArtifactOrder(String profileKey, List<ArtifactKey> order) {
    if (order.isEmpty()) {
      throw new ProfileConfigurationException("bootstrap.profile.artifact-order.empty", profileKey);
    }
    Set<ArtifactKey> ordered = new HashSet<>();
    for (var key : order) {
      if (!ordered.add(key)) {
        throw new ProfileConfigurationException(
            "bootstrap.profile.artifact-order.duplicate", profileKey, key.key());
      }
      requireArtifact(profileKey, key);
    }
    return ordered;
  }

  private static void validateOutputParents(String profileKey, Set<Path> outputs) {
    for (Path output : outputs) {
      for (Path parent = output.getParent(); parent != null; parent = parent.getParent()) {
        if (outputs.contains(parent)) {
          throw new ProfileConfigurationException(
              "bootstrap.profile.output.file-parent", profileKey, parent, output);
        }
      }
    }
  }

  public ArtifactDefinition requireArtifact(String profileKey, ArtifactKey artifactKey) {
    var profileProps = requireProfile(profileKey);

    var artifact = profileProps.artifacts().get(artifactKey.key());
    if (artifact == null) {
      throw new ProfileConfigurationException(
          ProfileConfigurationException.KEY_ARTIFACT_NOT_FOUND, artifactKey.key(), profileKey);
    }

    return artifact;
  }

  private static void validateArtifact(
      String profileKey, ArtifactKey artifactKey, ArtifactDefinition artifact) {
    var templates = artifact.templates();
    boolean validCount = switch (artifactKey) {
      case SOURCE_LAYOUT, ARCHITECTURE_GOVERNANCE, SAMPLE_CODE -> templates.isEmpty();
      case BUILD_TOOL_FILES -> !templates.isEmpty();
      default -> templates.size() == 1;
    };
    if (!validCount) {
      throw new ProfileConfigurationException(
          "bootstrap.artifact.templates.count", profileKey, artifactKey.key(), templates.size());
    }

    if (artifactKey != ArtifactKey.SOURCE_LAYOUT) {
      String basePath = artifact.templateBasePath();
      if (basePath == null || basePath.isBlank()) {
        throw new ProfileConfigurationException(
            ProfileConfigurationException.KEY_TEMPLATE_BASE_MISSING, profileKey, artifactKey.key());
      }
      requireRelativePath(profileKey, artifactKey, basePath);
    }

    Set<Path> outputs = new HashSet<>();
    for (var template : templates) {
      requireRelativePath(profileKey, artifactKey, template.template());
      Path output = requireRelativePath(profileKey, artifactKey, template.outputPath());
      if (!outputs.add(output)) {
        throw new ProfileConfigurationException(
            "bootstrap.artifact.template.output.duplicate", profileKey, artifactKey.key(), output);
      }
    }
    if (artifactKey == ArtifactKey.BUILD_TOOL_FILES
        && WRAPPER_OUTPUTS.stream().map(Path::of).anyMatch(path -> !outputs.contains(path))) {
      throw new ProfileConfigurationException(
          "bootstrap.artifact.wrapper.outputs.missing", profileKey, artifactKey.key());
    }
  }

  private static Path requireRelativePath(String profileKey, ArtifactKey key, String value) {
    try {
      Path path = Path.of(value);
      GeneratedFilePolicy.requireRelativePath(path);
      return path;
    } catch (InvalidPathException | DomainViolationException failure) {
      throw new ProfileConfigurationException(
          "bootstrap.artifact.template.path.invalid", failure, profileKey, key.key(), value);
    }
  }
}
