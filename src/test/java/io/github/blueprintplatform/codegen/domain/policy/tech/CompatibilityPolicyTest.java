package io.github.blueprintplatform.codegen.domain.policy.tech;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
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
class CompatibilityPolicyTest {

  private static TechStack techStack() {
    return new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);
  }

  private static PlatformTarget target(JavaVersion java, SpringBootVersion boot) {
    return new SpringBootJvmTarget(java, boot);
  }

  @Test
  @DisplayName("ensureCompatible(PlatformSpec) should delegate to core compatibility checks")
  void ensureCompatible_platformSpec_shouldWork() {
    PlatformSpec platform =
        new PlatformSpec(techStack(), target(JavaVersion.JAVA_21, SpringBootVersion.V4_2));

    assertThatCode(() -> CompatibilityPolicy.ensureCompatible(platform)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("ensureCompatible(PlatformSpec) should fail when platform is null")
  void ensureCompatible_platformSpec_null_shouldFailTargetMissing() {
    assertThatThrownBy(() -> CompatibilityPolicy.ensureCompatible(null))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));
  }

  @Test
  @DisplayName("ensureCompatible should fail when techStack or target is null")
  void ensureCompatible_nullTechStackOrTarget_shouldFailTargetMissing() {
    TechStack stack = techStack();
    PlatformTarget ok = target(JavaVersion.JAVA_21, SpringBootVersion.V4_2);

    assertThatThrownBy(() -> CompatibilityPolicy.ensureCompatible(null, ok))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));

    assertThatThrownBy(() -> CompatibilityPolicy.ensureCompatible(stack, null))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));

    assertThatThrownBy(() -> CompatibilityPolicy.ensureCompatible(null, null))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("platform.target.missing"));
  }

  @Test
  @DisplayName("ensureCompatible should accept all supported Spring Boot / Java combinations")
  void ensureCompatible_supportedTargets_shouldPass() {
    TechStack stack = techStack();

    assertThatCode(
            () ->
                CompatibilityPolicy.ensureCompatible(
                    stack, target(JavaVersion.JAVA_21, SpringBootVersion.V4_2)))
        .doesNotThrowAnyException();

    assertThatCode(
            () ->
                CompatibilityPolicy.ensureCompatible(
                    stack, target(JavaVersion.JAVA_25, SpringBootVersion.V4_2)))
        .doesNotThrowAnyException();

    assertThatCode(
            () ->
                CompatibilityPolicy.ensureCompatible(
                    stack, target(JavaVersion.JAVA_21, SpringBootVersion.V4_1)))
        .doesNotThrowAnyException();
  }

  @Test
  void rejectsPatchFromAnotherLine() {
    assertThatThrownBy(
            () -> new SpringBootJvmTarget(JavaVersion.JAVA_25, SpringBootVersion.V4_1, "4.2.0"))
        .isInstanceOf(DomainViolationException.class);
  }

  @Test
  @DisplayName("TechStack should fail fast when framework/buildTool/language is null")
  void techStack_nullParts_shouldFailTechStackNotBlank() {
    assertThatThrownBy(() -> new TechStack(null, BuildTool.MAVEN, Language.JAVA))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("project.tech-stack.not.blank"));

    assertThatThrownBy(() -> new TechStack(Framework.SPRING_BOOT, null, Language.JAVA))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("project.tech-stack.not.blank"));

    assertThatThrownBy(() -> new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, null))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("project.tech-stack.not.blank"));
  }

  @Test
  @DisplayName("PlatformTarget should be sealed and permit only SpringBootJvmTarget")
  void platformTarget_shouldBeSealedAndRestricted() {
    assertThat(PlatformTarget.class.isSealed()).isTrue();

    var permitted = PlatformTarget.class.getPermittedSubclasses();
    assertThat(permitted).containsExactly(SpringBootJvmTarget.class);
  }

  @Test
  @DisplayName("allSupportedTargets should return all combinations defined in the matrix")
  void allSupportedTargets_shouldReturnAllMatrixCombinations() {
    List<PlatformTarget> targets = CompatibilityPolicy.allSupportedTargets();

    assertThat(targets)
        .containsExactlyInAnyOrder(
            target(JavaVersion.JAVA_21, SpringBootVersion.V4_1),
            target(JavaVersion.JAVA_21, SpringBootVersion.V4_2),
            target(JavaVersion.JAVA_25, SpringBootVersion.V4_2),
            target(JavaVersion.JAVA_25, SpringBootVersion.V4_1));
  }
}
