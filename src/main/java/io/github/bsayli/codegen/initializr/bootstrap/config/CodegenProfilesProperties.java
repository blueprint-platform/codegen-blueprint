package io.github.bsayli.codegen.initializr.bootstrap.config;

import io.github.bsayli.codegen.initializr.adapter.profile.ProfileType;
import io.github.bsayli.codegen.initializr.bootstrap.error.ProfileConfigurationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "codegen")
public record CodegenProfilesProperties(@Valid @NotNull Map<String, ProfileProperties> profiles) {

  public ArtifactProperties artifact(ProfileType profile, String artifactKey) {
    var p = requireProfile(profile);
    var raw = requireArtifact(profile, p, artifactKey);
    String fullTemplate = p.templateBasePath() + "/" + raw.template();
    return new ArtifactProperties(raw.enabled(), fullTemplate, raw.outputPath());
  }

  ProfileProperties requireProfile(ProfileType profile) {
    var key = profile.key();
    var p = profiles.get(key);
    if (p == null) {
      throw new ProfileConfigurationException(
          ProfileConfigurationException.KEY_PROFILE_NOT_FOUND, key);
    }
    return p;
  }

  ArtifactProperties requireArtifact(ProfileType profile, ProfileProperties p, String artifactKey) {
    var a = p.artifacts().get(artifactKey);
    if (a == null) {
      throw new ProfileConfigurationException(
          ProfileConfigurationException.KEY_ARTIFACT_NOT_FOUND, artifactKey, profile.key());
    }
    return a;
  }

  public record ProfileProperties(
      @NotBlank String templateBasePath,
      @Valid @NotNull Map<String, ArtifactProperties> artifacts) {}
}
