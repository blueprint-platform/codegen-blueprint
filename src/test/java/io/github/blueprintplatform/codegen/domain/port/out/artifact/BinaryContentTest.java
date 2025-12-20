package io.github.blueprintplatform.codegen.domain.port.out.artifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("domain")
class BinaryContentTest {

  @Test
  @DisplayName("Ctor should reject null bytes with file.content.not.blank")
  void ctor_nullBytes_shouldFail() {
    assertThatThrownBy(() -> new BinaryContent(null))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            dve -> assertThat(dve.getMessageKey()).isEqualTo("file.content.not.blank"));
  }

  @Test
  @DisplayName("Should defensively copy in ctor and accessor")
  void shouldDefensivelyCopyCtorAndAccessor() {
    byte[] original = new byte[] {1, 2, 3};

    BinaryContent content = new BinaryContent(original);
    original[0] = 9;

    assertThat(content.bytes()).containsExactly(1, 2, 3);

    byte[] view = content.bytes();
    view[1] = 8;

    assertThat(content.bytes()).containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("equals/hashCode/toString should cover all branches")
  void equalsHashCodeToString_shouldCoverBranches() {
    BinaryContent a = new BinaryContent(new byte[] {1, 2, 3});
    BinaryContent equal = new BinaryContent(new byte[] {1, 2, 3});
    BinaryContent different = new BinaryContent(new byte[] {1, 2, 4});

    assertThat(a)
        .isEqualTo(a) // self
        .isEqualTo(equal) // same content
        .isNotEqualTo(different) // different content
        .isNotEqualTo(null); // null

    assertThat(a.equals("x")).isFalse();

    assertThat(a).hasSameHashCodeAs(equal);

    assertThat(a.toString()).contains("BinaryContent").contains("size=3");
  }
}
