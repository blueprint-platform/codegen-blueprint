package io.github.blueprintplatform.codegen.adapter.out.shared;

public record SampleCodeLayoutSpec(Roots roots, Levels levels) {

  public record Roots(String standard, String hexagonal) {}

  public record Levels(String basicDirName, String richDirName) {}
}
