package io.github.blueprintplatform.codegen.domain.policy.tech;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("domain")
class PlatformTargetSelectorTest {

  private static TechStack techStack() {
    return new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);
  }

  @Test
  @DisplayName("select with compatible target should return PlatformTarget")
  void select_compatibleTarget_shouldReturnRequestedTarget() {
    TechStack stack = techStack();

    PlatformTarget result =
        PlatformTargetSelector.select(stack, JavaVersion.JAVA_21, SpringBootVersion.V4_2);

    assertThat(result)
        .isInstanceOf(SpringBootJvmTarget.class)
        .extracting("java")
        .isEqualTo(JavaVersion.JAVA_21);

    assertThat(result).extracting("springBoot").isEqualTo(SpringBootVersion.V4_2);
  }

  @Test
  void selectsJava25ForBoot41() {
    assertThat(
            PlatformTargetSelector.select(techStack(), JavaVersion.JAVA_25, SpringBootVersion.V4_1))
        .isEqualTo(new SpringBootJvmTarget(JavaVersion.JAVA_25, SpringBootVersion.V4_1));
  }

  @Test
  @DisplayName("supportedTargetsFor should return all supported targets from CompatibilityPolicy")
  void supportedTargetsFor_shouldReturnAllSupportedTargets() {
    List<PlatformTarget> targets = PlatformTargetSelector.supportedTargetsFor();

    assertThat(targets)
        .isNotEmpty()
        .contains(new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2));
  }
}
