package ${projectPackageName}.architecture.archunit;

import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;

/**
 * Unknown-family guardrail (HEXAGONAL, context-scoped).
 * Purpose:
 * - Prevent silent guardrails degradation caused by renaming canonical package families
 *   inside a hexagonal bounded context (e.g. {@code adapter -> adapters}, {@code application -> applications}).
 * - Avoid "repo policing": packages outside detected bounded contexts are NOT restricted.
 * Heuristic (context detection):
 * - A sub-root is considered a bounded context if it contains {@code application} code.
 * Rule (within each detected context):
 * - Every class under that context root must be located under a canonical HEXAGONAL family as the FIRST segment:
 *   {@code adapter}, {@code application}, {@code domain}, {@code bootstrap}.
 * Notes:
 * - Works with both flat roots and nested sub-root structures.
 * - {@code bootstrap} is allowed but not required.
 * - This is a schema integrity rule, not a dependency rule.
 * Contract note:
 * - If this test fails, at least one bounded context contains code that does not conform
 *   to the generated HEXAGONAL package contract (likely a rename-based escape).
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalUnknownFamilyTest {

    private static final Set<String> ALLOWED_FAMILIES = Set.of(
            "adapter",
            "application",
            "domain",
            "bootstrap"
    );

    @ArchTest
    static void only_known_hexagonal_package_families_are_allowed_within_detected_contexts(JavaClasses classes) {
        var contexts = detectApplicationContexts(classes);

        if (contexts.isEmpty()) {
            Assertions.fail(
                    "No HEXAGONAL bounded context was detected under scope '" + BASE_PACKAGE + "'. "
                            + "Expected at least one context containing an 'application' package. "
                            + "This may indicate that the root package or canonical family names were changed."
            );
        }

        var violationsByContext = new TreeMap<String, Set<String>>();

        for (var contextRoot : contexts.keySet()) {
            var offenders = new LinkedHashSet<String>();

            for (var c : classes) {
                var pkg = c.getPackageName();
                if (pkg == null || pkg.isBlank()) {
                    continue;
                }
                if (!isUnderContext(pkg, contextRoot)) {
                    continue;
                }

                // IMPORTANT:
                // Do not let a parent context (e.g., BASE_PACKAGE) "swallow" nested bounded contexts.
                // If this class belongs to a *more specific* contextRoot, it will be validated there.
                if (belongsToAnotherContext(pkg, contextRoot, contexts)) {
                    continue;
                }

                if (!isInAllowedFamilyWithinContext(pkg, contextRoot)) {
                    offenders.add(c.getFullName());
                }
            }

            if (!offenders.isEmpty()) {
                violationsByContext.put(contextRoot, offenders);
            }
        }

        if (violationsByContext.isEmpty()) {
            return;
        }

        var message = new StringBuilder()
                .append("Unknown package families detected inside HEXAGONAL bounded contexts under base scope '")
                .append(BASE_PACKAGE).append("'. ")
                .append("Bounded contexts are inferred by presence of 'application'. ")
                .append("Within each detected context, every class must reside under the canonical families as the FIRST segment: ")
                .append(ALLOWED_FAMILIES).append(".\n\n")
                .append("Violations:\n");

        for (var e : violationsByContext.entrySet()) {
            message.append(" - ").append(e.getKey()).append(":\n");
            for (var cls : e.getValue()) {
                message.append("     - ").append(cls).append("\n");
            }
        }

        message.append("\nRemediation: keep canonical family names within each bounded context ")
                .append("(adapter/application/domain/bootstrap) as the FIRST segment under the context root, ")
                .append("or restructure intentionally and update the guardrails contract accordingly.");

        Assertions.fail(message.toString());
    }

    private static Map<String, Boolean> detectApplicationContexts(JavaClasses classes) {
        var contexts = new LinkedHashMap<String, Boolean>();

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }

            var contextRoot = contextRootForApplicationPackage(pkg);
            if (contextRoot == null || contextRoot.isBlank()) {
                continue;
            }

            contexts.putIfAbsent(contextRoot, Boolean.TRUE);
        }

        return contexts;
    }

    private static boolean belongsToAnotherContext(String packageName, String currentContextRoot, Map<String, Boolean> contexts) {
        for (var other : contexts.keySet()) {
            if (other.equals(currentContextRoot)) {
                continue;
            }
            // other is a more specific (nested) context under current
            if (isUnderContext(packageName, other) && isNestedContext(other, currentContextRoot)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNestedContext(String maybeNested, String maybeParent) {
        return maybeNested.startsWith(maybeParent + ".");
    }

    private static String contextRootForApplicationPackage(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return null;
        }

        var basePrefix = BASE_PACKAGE + ".";
        if (!packageName.startsWith(basePrefix)) {
            return null;
        }

        var token = ".application.";
        int idx = packageName.indexOf(token);
        if (idx >= 0) {
            return packageName.substring(0, idx);
        }

        token = ".application";
        idx = packageName.lastIndexOf(token);
        if (idx >= 0 && idx + token.length() == packageName.length()) {
            return packageName.substring(0, idx);
        }

        return null;
    }

    private static boolean isUnderContext(String packageName, String contextRoot) {
        return packageName.equals(contextRoot) || packageName.startsWith(contextRoot + ".");
    }

    private static boolean isInAllowedFamilyWithinContext(String packageName, String contextRoot) {
        if (packageName.equals(contextRoot)) {
            return true;
        }

        var remainder = packageName.substring(contextRoot.length());
        if (remainder.startsWith(".")) {
            remainder = remainder.substring(1);
        }
        if (remainder.isBlank()) {
            return true;
        }

        int dotIdx = remainder.indexOf('.');
        String firstSegment = dotIdx < 0 ? remainder : remainder.substring(0, dotIdx);

        return ALLOWED_FAMILIES.contains(firstSegment);
    }
}