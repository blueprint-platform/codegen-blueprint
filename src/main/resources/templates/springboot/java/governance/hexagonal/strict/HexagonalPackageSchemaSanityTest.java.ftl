package ${projectPackageName}.architecture.archunit;

import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_APPLICATION;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.PackageMatcher;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

/**
 * Hexagonal package schema sanity check (bounded context completeness).
 * <h2>Purpose</h2>
 * <ul>
 *   <li>Prevent silent architectural drift when teams introduce or modify bounded contexts.</li>
 *   <li>Ensure that every detected HEXAGONAL bounded context explicitly declares the canonical families.</li>
 * </ul>
 * <h2>Bounded Context Detection</h2>
 * <ul>
 *   <li>A bounded context is inferred by the presence of an {@code application} package.</li>
 *   <li>The context root is derived from the package prefix preceding {@code .application}.</li>
 *   <li>Multiple bounded contexts (nested or sibling) are supported.</li>
 * </ul>
 * <h2>Schema Contract (STRICT)</h2>
 * For every detected bounded context root, these canonical families must exist:
 * {@code adapter}, {@code application}, {@code domain}.
 * <h2>Notes</h2>
 * <ul>
 *   <li>This is a schema integrity guardrail, not a dependency rule.</li>
 *   <li>Packages outside detected bounded contexts are not restricted.</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalPackageSchemaSanityTest {

    private static final PackageMatcher APPLICATION_MATCHER = PackageMatcher.of(familyPattern(FAMILY_APPLICATION));
    private static final PackageMatcher ADAPTER_MATCHER = PackageMatcher.of(familyPattern(FAMILY_ADAPTER));
    private static final PackageMatcher DOMAIN_MATCHER = PackageMatcher.of(familyPattern(FAMILY_DOMAIN));

    // Implementation details for robust slicing/detection (NOT contract).
    private static final String APPLICATION_DOTTED_TOKEN = "." + FAMILY_APPLICATION + ".";
    private static final String APPLICATION_END_TOKEN = "." + FAMILY_APPLICATION;

    private static final int MAX_NEAR_MISS_EXAMPLES = 3;

    @ArchTest
    static void each_hexagonal_bounded_context_must_contain_canonical_families(JavaClasses classes) {
        var contexts = detectContexts(classes);

        if (contexts.isEmpty()) {
            Assertions.fail(
                    "No HEXAGONAL bounded context was detected under scope '" + BASE_PACKAGE + "'. "
                            + "A bounded context is inferred by presence of an '" + FAMILY_APPLICATION + "' package. "
                            + "This may indicate that the root package or canonical family names were changed."
            );
        }

        var allContexts = contexts.stream().collect(Collectors.toMap(c -> c, c -> Boolean.TRUE));
        var violations = new ArrayList<ContextViolation>();

        for (var contextRoot : contexts) {
            boolean hasApplication = containsFamilyInExactContext(classes, contextRoot, APPLICATION_MATCHER, allContexts);
            boolean hasAdapter = containsFamilyInExactContext(classes, contextRoot, ADAPTER_MATCHER, allContexts);
            boolean hasDomain = containsFamilyInExactContext(classes, contextRoot, DOMAIN_MATCHER, allContexts);

            if (hasAdapter && hasApplication && hasDomain) {
                continue;
            }

            var evidence = applicationEvidenceUnderContext(classes, contextRoot);

            var missingDetails = new LinkedHashMap<String, MissingFamilyDetail>();
            if (!hasAdapter) {
                missingDetails.put(FAMILY_ADAPTER, missingFamilyDetail(classes, contextRoot, FAMILY_ADAPTER));
            }
            if (!hasApplication) {
                missingDetails.put(FAMILY_APPLICATION, missingFamilyDetail(classes, contextRoot, FAMILY_APPLICATION));
            }
            if (!hasDomain) {
                missingDetails.put(FAMILY_DOMAIN, missingFamilyDetail(classes, contextRoot, FAMILY_DOMAIN));
            }

            violations.add(new ContextViolation(
                    contextRoot,
                    hasAdapter,
                    hasApplication,
                    hasDomain,
                    evidence,
                    missingDetails
            ));
        }

        if (violations.isEmpty()) {
            return;
        }

        Assertions.fail(buildViolationMessage(violations));
    }

    private static String buildViolationMessage(List<ContextViolation> violations) {
        var sb = new StringBuilder()
                .append("HEXAGONAL package schema integrity failure under base scope '")
                .append(BASE_PACKAGE)
                .append("'.\n\n")
                .append("Bounded contexts are inferred by presence of '")
                .append(FAMILY_APPLICATION)
                .append("'. For each detected context root, the canonical families must exist: ")
                .append("[")
                .append(FAMILY_ADAPTER).append(", ")
                .append(FAMILY_APPLICATION).append(", ")
                .append(FAMILY_DOMAIN)
                .append("].\n\n")
                .append("Violations:\n");

        for (var v : violations) {
            sb.append(" - context: ").append(v.contextRoot()).append("\n");
            sb.append("     context evidence: ").append(v.applicationEvidence() == null ? "<unknown>" : v.applicationEvidence()).append("\n");

            sb.append("     present: ")
                    .append(FAMILY_ADAPTER).append(v.hasAdapter() ? " ✅" : " ❌").append(", ")
                    .append(FAMILY_APPLICATION).append(v.hasApplication() ? " ✅" : " ❌").append(", ")
                    .append(FAMILY_DOMAIN).append(v.hasDomain() ? " ✅" : " ❌")
                    .append("\n");

            sb.append("     missing: ");
            var missing = new ArrayList<String>();
            if (!v.hasAdapter()) missing.add(FAMILY_ADAPTER);
            if (!v.hasApplication()) missing.add(FAMILY_APPLICATION);
            if (!v.hasDomain()) missing.add(FAMILY_DOMAIN);
            sb.append(String.join(", ", missing)).append("\n");

            if (!v.missingFamilyDetails().isEmpty()) {
                sb.append("     cause:\n");
                for (var e : v.missingFamilyDetails().entrySet()) {
                    var detail = e.getValue();

                    sb.append("         - missing family root: ").append(detail.expectedRoot()).append("\n");

                    if (detail.nearMissExamples().isEmpty()) {
                        sb.append("           near-miss candidates: <none>\n");
                    } else {
                        sb.append("           near-miss candidates:\n");
                        for (var ex : detail.nearMissExamples()) {
                            sb.append("             - ").append(ex).append("\n");
                        }
                    }

                    if (detail.likelyCause() != null && !detail.likelyCause().isBlank()) {
                        sb.append("           likely cause: ").append(detail.likelyCause()).append("\n");
                    }
                }
            }
        }

        sb.append("\nRemediation:\n")
                .append(" - If you introduced a sub-root (bounded context), ensure it contains ")
                .append(FAMILY_ADAPTER).append("/").append(FAMILY_APPLICATION).append("/").append(FAMILY_DOMAIN).append(" families.\n")
                .append(" - If you renamed a canonical family (e.g., '").append(FAMILY_ADAPTER).append("' -> 'adapters'), revert the rename ")
                .append("or update the guardrails contract intentionally.\n")
                .append(" - If you refactored the root package name, ensure BASE_PACKAGE matches the new root.\n");

        return sb.toString();
    }

    private static Set<String> detectContexts(JavaClasses classes) {
        var contexts = new TreeSet<String>();

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }
            if (!APPLICATION_MATCHER.matches(pkg)) {
                continue;
            }
            contexts.add(contextRootForApplicationPackage(pkg));
        }

        return contexts;
    }

    private static boolean containsFamilyInExactContext(
            JavaClasses classes,
            String contextRoot,
            PackageMatcher familyMatcher,
            Map<String, Boolean> allContexts
    ) {
        var prefix = contextRoot + ".";

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }

            if (!(pkg.equals(contextRoot) || pkg.startsWith(prefix))) {
                continue;
            }

            if (belongsToMoreSpecificContext(pkg, contextRoot, allContexts)) {
                continue;
            }

            if (familyMatcher.matches(pkg)) {
                return true;
            }
        }

        return false;
    }

    private static boolean belongsToMoreSpecificContext(
            String packageName,
            String currentContextRoot,
            Map<String, Boolean> allContexts
    ) {
        for (var other : allContexts.keySet()) {
            if (other.equals(currentContextRoot)) {
                continue;
            }
            if (isNestedContext(other, currentContextRoot) && isUnderContext(packageName, other)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNestedContext(String maybeNested, String maybeParent) {
        return maybeNested.startsWith(maybeParent + ".");
    }

    private static boolean isUnderContext(String packageName, String contextRoot) {
        return packageName.equals(contextRoot) || packageName.startsWith(contextRoot + ".");
    }

    private static String contextRootForApplicationPackage(String packageName) {
        var basePrefix = BASE_PACKAGE + ".";
        if (!packageName.startsWith(basePrefix)) {
            return BASE_PACKAGE;
        }

        int idx = indexOfFamilyToken(packageName, familyToken(FAMILY_APPLICATION), familyNameAtEnd(FAMILY_APPLICATION));
        if (idx < 0) {
            return BASE_PACKAGE;
        }

        var root = packageName.substring(0, idx);
        return root.isBlank() ? BASE_PACKAGE : root;
    }

    private static int indexOfFamilyToken(String packageName, String dottedToken, String endToken) {
        int idx = packageName.indexOf(dottedToken);
        if (idx >= 0) {
            return idx;
        }

        idx = packageName.lastIndexOf(endToken);
        if (idx >= 0 && idx + endToken.length() == packageName.length()) {
            return idx;
        }

        idx = packageName.indexOf(familyToken(FAMILY_ADAPTER));
        if (idx >= 0) {
            return idx;
        }
        idx = packageName.indexOf(familyToken(FAMILY_DOMAIN));
        if (idx >= 0) {
            return idx;
        }

        idx = packageName.lastIndexOf(familyNameAtEnd(FAMILY_ADAPTER));
        if (idx >= 0 && idx + familyNameAtEnd(FAMILY_ADAPTER).length() == packageName.length()) {
            return idx;
        }
        idx = packageName.lastIndexOf(familyNameAtEnd(FAMILY_DOMAIN));
        if (idx >= 0 && idx + familyNameAtEnd(FAMILY_DOMAIN).length() == packageName.length()) {
            return idx;
        }

        return -1;
    }

    private static String applicationEvidenceUnderContext(JavaClasses classes, String contextRoot) {
        var prefix = contextRoot + ".";
        String best = null;
        int bestDepth = Integer.MAX_VALUE;

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }
            if (!(pkg.equals(contextRoot) || pkg.startsWith(prefix))) {
                continue;
            }
            if (!APPLICATION_MATCHER.matches(pkg)) {
                continue;
            }

            var normalized = normalizeToApplicationRoot(pkg);
            if (normalized == null) {
                continue;
            }

            int depth = segmentCount(normalized);

            if (depth < bestDepth) {
                best = normalized;
                bestDepth = depth;
            } else if (depth == bestDepth && best != null && normalized.compareTo(best) < 0) {
                best = normalized;
            } else if (depth == bestDepth && best == null) {
                best = normalized;
            }
        }

        return best;
    }

    private static String normalizeToApplicationRoot(String packageName) {
        int idx = packageName.indexOf(APPLICATION_DOTTED_TOKEN);
        if (idx >= 0) {
            return packageName.substring(0, idx + APPLICATION_END_TOKEN.length());
        }

        idx = packageName.lastIndexOf(APPLICATION_END_TOKEN);
        if (idx >= 0 && idx + APPLICATION_END_TOKEN.length() == packageName.length()) {
            return packageName;
        }

        return null;
    }

    private static MissingFamilyDetail missingFamilyDetail(JavaClasses classes, String contextRoot, String missingFamily) {
        var expectedRoot = contextRoot + "." + missingFamily;

        var examples = new LinkedHashSet<String>();

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }
            if (!isUnderContext(pkg, contextRoot)) {
                continue;
            }

            var first = firstSegmentAfterContext(pkg, contextRoot);
            if (first == null) {
                continue;
            }

            if (isNearMissFamily(first, missingFamily)) {
                examples.add(first + " (e.g. " + c.getFullName() + ")");
                if (examples.size() >= MAX_NEAR_MISS_EXAMPLES) {
                    break;
                }
            }
        }

        var likelyCause = examples.isEmpty()
                ? "context is incomplete (canonical family missing)"
                : "likely rename-escape (non-canonical family name present)";

        return new MissingFamilyDetail(
                expectedRoot,
                List.copyOf(examples),
                likelyCause
        );
    }

    private static boolean isNearMissFamily(String firstSegment, String canonicalFamily) {
        if (firstSegment == null || firstSegment.isBlank()) {
            return false;
        }
        if (firstSegment.equals(canonicalFamily + "s")) {
            return true;
        }
        if (firstSegment.startsWith(canonicalFamily)) {
            return true;
        }
        if (canonicalFamily.equals(FAMILY_ADAPTER) && firstSegment.startsWith("adapt")) {
            return true;
        }
        return false;
    }

    private static String firstSegmentAfterContext(String packageName, String contextRoot) {
        if (packageName == null || packageName.isBlank()) {
            return null;
        }
        if (packageName.equals(contextRoot)) {
            return null;
        }
        var prefix = contextRoot + ".";
        if (!packageName.startsWith(prefix)) {
            return null;
        }

        int start = prefix.length();
        int dot = packageName.indexOf('.', start);
        if (dot < 0) {
            return packageName.substring(start);
        }
        return packageName.substring(start, dot);
    }

    private static int segmentCount(String packageName) {
        int segments = 1;
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == '.') {
                segments++;
            }
        }
        return segments;
    }

    private static String familyToken(String family) {
        return "." + family + ".";
    }

    private static String familyNameAtEnd(String family) {
        return "." + family;
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }

    private record ContextViolation(
            String contextRoot,
            boolean hasAdapter,
            boolean hasApplication,
            boolean hasDomain,
            String applicationEvidence,
            Map<String, MissingFamilyDetail> missingFamilyDetails
    ) {}

    private record MissingFamilyDetail(
            String expectedRoot,
            List<String> nearMissExamples,
            String likelyCause
    ) {}
}