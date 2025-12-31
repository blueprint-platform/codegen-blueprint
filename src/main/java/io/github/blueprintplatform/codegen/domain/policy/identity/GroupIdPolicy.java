package io.github.blueprintplatform.codegen.domain.policy.identity;

import io.github.blueprintplatform.codegen.domain.error.code.ErrorCode;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.policy.rule.DotSeparatedSegmentsRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.LengthBetweenRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.NotBlankRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.base.CompositeRule;
import io.github.blueprintplatform.codegen.domain.policy.rule.base.Rule;
import java.util.Locale;
import java.util.regex.Pattern;

public final class GroupIdPolicy {

  private static final int MIN = 3;
  private static final int MAX = 100;

  private static final Pattern SEGMENT = Pattern.compile("^[a-z][a-z0-9]*$");

  private static final ErrorCode CODE_NOT_BLANK = () -> "project.group-id.not.blank";
  private static final ErrorCode CODE_LENGTH = () -> "project.group-id.length";
  private static final ErrorCode CODE_SEGMENT_FORMAT = () -> "project.group-id.segment.format";

  private GroupIdPolicy() {}

  public static String enforce(String raw) {
    String n = normalize(raw);
    validate(n);
    return n;
  }

  private static String normalize(String raw) {
    if (raw == null) {
      throw new DomainViolationException(CODE_NOT_BLANK);
    }
    return raw.trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
  }

  private static void validate(String value) {
    Rule<String> rule =
        CompositeRule.of(
            new NotBlankRule(CODE_NOT_BLANK),
            new LengthBetweenRule(MIN, MAX, CODE_LENGTH),
            new DotSeparatedSegmentsRule(SEGMENT, CODE_SEGMENT_FORMAT));
    rule.check(value);
  }
}
