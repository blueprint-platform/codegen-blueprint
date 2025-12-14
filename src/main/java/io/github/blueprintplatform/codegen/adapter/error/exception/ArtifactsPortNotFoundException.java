package io.github.blueprintplatform.codegen.adapter.error.exception;

public final class ArtifactsPortNotFoundException extends AdapterException {

  private static final String KEY = "adapter.artifacts.port.not.found";
  private final String profileKey;

  public ArtifactsPortNotFoundException(String profileKey) {
    super(KEY, profileKey);
    this.profileKey = profileKey;
  }

  public String getProfileKey() {
    return profileKey;
  }
}
