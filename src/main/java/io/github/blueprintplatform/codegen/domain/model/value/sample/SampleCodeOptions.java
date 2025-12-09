package io.github.blueprintplatform.codegen.domain.model.value.sample;

public record SampleCodeOptions(SampleCodeLevel level) {

  public SampleCodeOptions {
    if (level == null) {
      level = SampleCodeLevel.NONE;
    }
  }

  public static SampleCodeOptions none() {
    return new SampleCodeOptions(SampleCodeLevel.NONE);
  }

  public static SampleCodeOptions basic() {
    return new SampleCodeOptions(SampleCodeLevel.BASIC);
  }

  public static SampleCodeOptions rich() {
    return new SampleCodeOptions(SampleCodeLevel.RICH);
  }

  public boolean isEnabled() {
    return level.isEnabled();
  }
}
