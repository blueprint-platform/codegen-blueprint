package io.github.blueprintplatform.codegen.adapter.out.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import io.github.blueprintplatform.codegen.adapter.error.exception.artifact.ArtifactsPortNotFoundException;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ProfileBasedArtifactsSelectorTest {

  @Test
  @DisplayName(
      "select() should throw ArtifactsPortNotFoundException when no port registered for computed key")
  void select_shouldThrowWhenPortMissing() {
    TechStack opts = new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);

    ProfileBasedArtifactsSelector selector = new ProfileBasedArtifactsSelector(Map.of());

    String expectedKey = TechStackProfileKey.from(opts);

    assertThatThrownBy(() -> selector.select(opts))
        .isInstanceOfSatisfying(
            ArtifactsPortNotFoundException.class,
            ex -> assertThat(ex.getProfileKey()).isEqualTo(expectedKey));
  }

  @Test
  @DisplayName("select() should return registered ProjectArtifactsPort for matching profile key")
  void select_shouldReturnMatchingPort() {
    TechStack opts = new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);

    String key = TechStackProfileKey.from(opts);

    ProjectArtifactsPort port = mock(ProjectArtifactsPort.class);

    ProfileBasedArtifactsSelector selector = new ProfileBasedArtifactsSelector(Map.of(key, port));

    ProjectArtifactsPort result = selector.select(opts);

    assertThat(result).isSameAs(port);
  }
}
