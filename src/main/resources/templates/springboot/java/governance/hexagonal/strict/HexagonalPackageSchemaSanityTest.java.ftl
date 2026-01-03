package ${projectPackageName}.architecture.archunit;

import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.PackageMatcher;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Assertions;

/**
 * Hexagonal package schema sanity check (bounded context completeness).
 * Purpose:
 * - Prevent silent guardrails degradation when teams introduce multiple sub-roots (bounded contexts).
 * - Ensure every detected hexagonal bounded context contains the canonical families.
 * Heuristic (context detection):
 * - A bounded context is detected if it contains {@code application} code.
 * Guarantees:
 * - For every detected bounded context, the following canonical families MUST exist:
 *   {@code adapter}, {@code application}, {@code domain}.
 * Notes:
 * - Works for both flat roots and nested sub-root structures.
 * - Does not restrict how many bounded contexts exist.
 * - Schema integrity rule (not a dependency rule).
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalPackageSchemaSanityTest {

    private static final String FAMILY_ADAPTER = "adapter";
    private static final String FAMILY_APPLICATION = "application";
    private static final String FAMILY_DOMAIN = "domain";

    private static final String TOKEN_ADAPTER_DOT = ".adapter.";
    private static final String TOKEN_APPLICATION_DOT = ".application.";
    private static final String TOKEN_DOMAIN_DOT = ".domain.";

    private static final String TOKEN_ADAPTER_END = ".adapter";
    private static final String TOKEN_APPLICATION_END = ".application";
    private static final String TOKEN_DOMAIN_END = ".domain";

    private static final PackageMatcher APPLICATION_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..application..");
    private static final PackageMatcher ADAPTER_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..adapter..");
    private static final PackageMatcher DOMAIN_MATCHER = PackageMatcher.of(BASE_PACKAGE + "..domain..");

    @ArchTest
    static void each_hexagonal_bounded_context_must_contain_canonical_families(JavaClasses classes) {
        var contexts = detectContexts(classes);

        if (contexts.isEmpty()) {
            Assertions.fail(
                    "No hexagonal bounded context was detected under scope '" + BASE_PACKAGE + "'. "
                            + "Expected at least one context containing an 'application' package. "
                            + "This may indicate that the root package or canonical family names were changed."
            );
        }

        var violations = new ArrayList<String>();
        var allContexts = contexts.stream().collect(java.util.stream.Collectors.toMap(c -> c, c -> Boolean.TRUE));

        for (var contextRoot : contexts) {
            boolean hasApplication = containsFamilyInExactContext(classes, contextRoot, APPLICATION_MATCHER, allContexts);
            boolean hasAdapter = containsFamilyInExactContext(classes, contextRoot, ADAPTER_MATCHER, allContexts);
            boolean hasDomain = containsFamilyInExactContext(classes, contextRoot, DOMAIN_MATCHER, allContexts);

            var missing = new ArrayList<String>();
            if (!hasAdapter) missing.add(FAMILY_ADAPTER);
            if (!hasApplication) missing.add(FAMILY_APPLICATION);
            if (!hasDomain) missing.add(FAMILY_DOMAIN);

            if (!missing.isEmpty()) {
                violations.add(contextRoot + " missing: " + String.join(", ", missing));
            }
        }

        if (violations.isEmpty()) {
            return;
        }

        Assertions.fail(
                "Hexagonal guardrails require each bounded context to contain canonical package families "
                        + "under scope '" + BASE_PACKAGE + "'. Violations:\n - "
                        + String.join("\n - ", violations)
                        + "\nIf you renamed a package family (e.g., 'adapter' -> 'adapters') or moved code into a sub-root, "
                        + "either revert to canonical family names or update the guardrails contract intentionally."
        );
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

            // IMPORTANT:
            // Do not let a parent context (e.g., BASE_PACKAGE) count families that only exist in nested contexts.
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

    private static String contextRootForApplicationPackage(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return BASE_PACKAGE;
        }

        var basePrefix = BASE_PACKAGE + ".";
        if (!packageName.startsWith(basePrefix)) {
            return BASE_PACKAGE;
        }

        int idx = indexOfFamilyToken(packageName, TOKEN_APPLICATION_DOT, TOKEN_APPLICATION_END);
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

        idx = packageName.indexOf(TOKEN_ADAPTER_DOT);
        if (idx >= 0) {
            return idx;
        }
        idx = packageName.indexOf(TOKEN_DOMAIN_DOT);
        if (idx >= 0) {
            return idx;
        }

        idx = packageName.lastIndexOf(TOKEN_ADAPTER_END);
        if (idx >= 0 && idx + TOKEN_ADAPTER_END.length() == packageName.length()) {
            return idx;
        }
        idx = packageName.lastIndexOf(TOKEN_DOMAIN_END);
        if (idx >= 0 && idx + TOKEN_DOMAIN_END.length() == packageName.length()) {
            return idx;
        }

        return -1;
    }
}