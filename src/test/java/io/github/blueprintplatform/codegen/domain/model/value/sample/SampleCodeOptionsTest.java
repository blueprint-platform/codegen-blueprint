package io.github.blueprintplatform.codegen.domain.model.value.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;

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
  void rejectsRichLevelAtTheDomainBoundary() {
    assertThatThrownBy(() -> SampleCodeLevel.fromKey("rich"))
        .isInstanceOf(DomainViolationException.class)
        .hasMessage("project.sample.level.unknown");
  }
}
