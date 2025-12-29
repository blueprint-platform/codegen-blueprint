package io.github.blueprintplatform.codegen.domain.model.value.architecture;

public record ArchitectureGovernance(GuardrailsMode mode) {

  public ArchitectureGovernance {
    if (mode == null) {
      mode = GuardrailsMode.NONE;
    }
  }

  public static ArchitectureGovernance none() {
    return new ArchitectureGovernance(GuardrailsMode.NONE);
  }

  public static ArchitectureGovernance basic() {
    return new ArchitectureGovernance(GuardrailsMode.BASIC);
  }

  public static ArchitectureGovernance strict() {
    return new ArchitectureGovernance(GuardrailsMode.STRICT);
  }

  public boolean isEnabled() {
    return mode.isEnabled();
  }

  public boolean isStrict() {
    return mode == GuardrailsMode.STRICT;
  }

  public boolean isBasic() {
    return mode == GuardrailsMode.BASIC;
  }
}
