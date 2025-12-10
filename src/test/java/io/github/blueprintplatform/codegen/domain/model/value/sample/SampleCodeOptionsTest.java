package io.github.blueprintplatform.codegen.domain.model.value.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("domain")
class SampleCodeOptionsTest {

  @Test
  @DisplayName("null level should default to NONE and be disabled")
  void nullLevel_shouldDefaultToNoneAndBeDisabled() {
    SampleCodeOptions options = new SampleCodeOptions(null);

    assertThat(options.level()).isEqualTo(SampleCodeLevel.NONE);
    assertThat(options.isEnabled()).isFalse();
  }

  @Test
  @DisplayName("none() factory should create disabled options with NONE level")
  void noneFactory_shouldCreateDisabledNoneLevel() {
    SampleCodeOptions options = SampleCodeOptions.none();

    assertThat(options.level()).isEqualTo(SampleCodeLevel.NONE);
    assertThat(options.isEnabled()).isFalse();
  }

  @Test
  @DisplayName("basic() factory should create enabled options with BASIC level")
  void basicFactory_shouldCreateEnabledBasicLevel() {
    SampleCodeOptions options = SampleCodeOptions.basic();

    assertThat(options.level()).isEqualTo(SampleCodeLevel.BASIC);
    assertThat(options.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("rich() factory should create enabled options with RICH level")
  void richFactory_shouldCreateEnabledRichLevel() {
    SampleCodeOptions options = SampleCodeOptions.rich();

    assertThat(options.level()).isEqualTo(SampleCodeLevel.RICH);
    assertThat(options.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("isEnabled should be false only for NONE")
  void isEnabled_shouldBeFalseOnlyForNone() {
    assertThat(SampleCodeOptions.none().isEnabled()).isFalse();
    assertThat(SampleCodeOptions.basic().isEnabled()).isTrue();
    assertThat(SampleCodeOptions.rich().isEnabled()).isTrue();
  }
}
