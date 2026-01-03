package ${projectPackageName}.architecture.archunit;

import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.PackageMatcher;
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
 * Standard (layered) package schema sanity check (bounded-context heuristic).
 * Purpose:
 * - Prevent silent architecture drift when teams rename or relocate core package families after generation.
 * - Validate schema completeness per bounded context.
 * Heuristic (context detection):
 * - Any context root that contains {@code controller} is treated as a bounded context.
 * Guarantees (per detected context):
 * - If a context has {@code controller}, it MUST also have {@code service} and {@code domain}.
 * Notes:
 * - Works with both flat roots and nested sub-root structures.
 * - {@code repository} is intentionally NOT required (persistence is optional).
 * - Schema integrity rule (not a dependency rule).
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardPackageSchemaSanityTest {

    private static final Set<String> REQUIRED_FAMILIES = Set.of("service", "domain");

    private static final String TOKEN_CONTROLLER_DOT = ".controller.";
    private static final String TOKEN_CONTROLLER_END = ".controller";

    private static final PackageMatcher CONTROLLER_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..controller..");
    private static final PackageMatcher SERVICE_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..service..");
    private static final PackageMatcher DOMAIN_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..domain..");

    @ArchTest
    static void bounded_contexts_with_controller_must_also_have_service_and_domain(JavaClasses classes) {
        var contexts = detectControllerContexts(classes);

        if (contexts.isEmpty()) {
            return;
        }

        var violations = new TreeMap<String, String>();

        for (var contextRoot : contexts.keySet()) {
            boolean hasService = containsFamilyInExactContext(classes, contextRoot, SERVICE_MATCHER, contexts);
            boolean hasDomain = containsFamilyInExactContext(classes, contextRoot, DOMAIN_MATCHER, contexts);

            if (hasService && hasDomain) {
                continue;
            }

            var missing = new LinkedHashSet<String>();
            if (!hasService) missing.add("service");
            if (!hasDomain) missing.add("domain");

            violations.put(
                    contextRoot,
                    "missing: " + String.join(", ", missing) + " (expected under '" + contextRoot + "')"
            );
        }

        if (violations.isEmpty()) {
            return;
        }

        var message = new StringBuilder()
                .append("Standard guardrails schema violation under base scope '").append(BASE_PACKAGE).append("'. ")
                .append("Bounded contexts are inferred by presence of 'controller'. ")
                .append("For each detected context, 'service' and 'domain' must also exist.\n\n")
                .append("Violations:\n");

        for (var v : violations.entrySet()) {
            message.append(" - ").append(v.getKey()).append(": ").append(v.getValue()).append("\n");
        }

        message.append("\nRemediation: revert to canonical family names (controller/service/domain) within each context, ")
                .append("or restructure intentionally and update the guardrails contract accordingly.");

        Assertions.fail(message.toString());
    }

    private static Map<String, Boolean> detectControllerContexts(JavaClasses classes) {
        var contexts = new LinkedHashMap<String, Boolean>();

        for (var c : classes) {
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                continue;
            }
            if (!CONTROLLER_MATCHER.matches(pkg)) {
                continue;
            }

            var contextRoot = contextRootForControllerPackage(pkg);
            if (contextRoot == null || contextRoot.isBlank()) {
                continue;
            }

            contexts.putIfAbsent(contextRoot, Boolean.TRUE);
        }

        return contexts;
    }

    private static String contextRootForControllerPackage(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return null;
        }

        var basePrefix = BASE_PACKAGE + ".";
        if (!packageName.startsWith(basePrefix)) {
            return null;
        }

        int idx = packageName.indexOf(TOKEN_CONTROLLER_DOT);
        if (idx >= 0) {
            return packageName.substring(0, idx);
        }

        idx = packageName.lastIndexOf(TOKEN_CONTROLLER_END);
        if (idx >= 0 && idx + TOKEN_CONTROLLER_END.length() == packageName.length()) {
            return packageName.substring(0, idx);
        }

        return null;
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

    private static boolean belongsToMoreSpecificContext(String packageName, String currentContextRoot, Map<String, Boolean> allContexts) {
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
}