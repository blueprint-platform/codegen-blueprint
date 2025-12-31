package io.github.blueprintplatform.codegen.domain.policy.identity;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.policy.rule.AllowedCharsRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.LengthBetweenRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.NoEdgeCharRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.NotBlankRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.StartsWithLetterRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.base.CompositeRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.base.Rule;
import java.util.Locale;

public final class ArtifactIdPolicy {

  private static final int MIN = 3;
  private static final int MAX = 50;

  private static final ErrorCode CODE_NOT_BLANK = () -> "project.artifact-id.not.blank";
  private static final ErrorCode CODE_LENGTH = () -> "project.artifact-id.length";
  private static final ErrorCode CODE_INVALID_CHARS = () -> "project.artifact-id.invalid.chars";
  private static final ErrorCode CODE_STARTS_WITH_LETTER =
      () -> "project.artifact-id.starts.with.letter";
  private static final ErrorCode CODE_EDGE_CHAR = () -> "project.artifact-id.edge.char";

  private ArtifactIdPolicy() {}

  public static String enforce(String raw) {
    String n = normalize(raw);
    validate(n);
    return n;
  }

  private static String normalize(String raw) {
    if (raw == null) {
      throw new DomainViolationException(CODE_NOT_BLANK);
    }
    return raw.trim()
        .replaceAll("\\s+", "-")
        .replace('_', '-')
        .toLowerCase(Locale.ROOT)
        .replaceAll("-{2,}", "-");
  }

  private static void validate(String value) {
    Rule<String> rule =
        CompositeRule.of(
            new NotBlankRule(CODE_NOT_BLANK),
            new LengthBetweenRule(MIN, MAX, CODE_LENGTH),
            new AllowedCharsRule("[a-z0-9-]", CODE_INVALID_CHARS),
            new StartsWithLetterRule(CODE_STARTS_WITH_LETTER),
            new NoEdgeCharRule('-', CODE_EDGE_CHAR));
    rule.check(value);
  }
}
