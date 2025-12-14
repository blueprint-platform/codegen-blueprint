package io.github.blueprintplatform.codegen.bootstrap.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SampleCodeProperties(@Valid @NotNull Roots roots, @Valid @NotNull Levels levels) {

  public record Roots(@NotBlank String standard, @NotBlank String hexagonal) {}

  public record Levels(@NotBlank String basicDirName, @NotBlank String richDirName) {}
}
