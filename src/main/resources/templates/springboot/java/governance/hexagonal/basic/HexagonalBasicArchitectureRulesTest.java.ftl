package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_APPLICATION;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_BOOTSTRAP;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.PackageMatcher;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.ArrayList;
import java.util.TreeSet;
import org.junit.jupiter.api.Assertions;

/**
 * Basic HEXAGONAL architecture guardrails.
 * Guarantees:
 * - Application code must not depend on adapter code.
 * - Bootstrap package is a leaf and must not be depended on by other packages.
 * - Each detected bounded context root is free of cyclic dependencies across its top-level packages.
 * Bounded context detection (deterministic):
 * - A bounded context root is defined as the package prefix before ".application.".
 * - Contexts are detected by the presence of at least one class under an "application" package.
 * Contract notes:
 * - Canonical family names are defined centrally in {@link HexagonalGuardrailsScope}.
 * - This rule intentionally derives patterns locally to keep behavior explicit and rule-scoped.
 * - This is a dependency and structure rule set; schema integrity is enforced separately.
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalBasicArchitectureRulesTest {

    private static final String APPLICATION_TOKEN = familyToken(FAMILY_APPLICATION);
    private static final PackageMatcher APPLICATION_MATCHER = PackageMatcher.of(familyPattern(FAMILY_APPLICATION));

    @ArchTest
    static final ArchRule application_must_not_depend_on_adapters =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_APPLICATION))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_ADAPTER))
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule outside_packages_must_not_depend_on_bootstrap =
            noClasses()
                    .that()
                    .resideOutsideOfPackage(familyPattern(FAMILY_BOOTSTRAP))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_BOOTSTRAP))
                    .allowEmptyShould(true);

    @ArchTest
    static void each_bounded_context_root_must_be_free_of_cycles(JavaClasses classes) {
        var contexts = detectContextRoots(classes);

        if (contexts.isEmpty()) {
            Assertions.fail(
                    "No HEXAGONAL bounded context was detected under scope '" + BASE_PACKAGE + "'. "
                            + "A bounded context is inferred by presence of an '" + FAMILY_APPLICATION + "' package."
            );
        }

        var violations = new ArrayList<String>();

        for (var contextRoot : contexts) {
            var rule =
                    slices()
                            .matching(contextRoot + ".(*)..")
                            .should()
                            .beFreeOfCycles()
                            .allowEmptyShould(true);

            var evaluation = rule.evaluate(classes);

            if (evaluation.hasViolation()) {
                violations.add(contextRoot);
            }
        }

        if (!violations.isEmpty()) {
            Assertions.fail(
                    "HEXAGONAL cyclic dependency violation(s) detected under base scope '" + BASE_PACKAGE + "'.\n\n"
                            + "The following bounded context root(s) have cycles across their top-level packages:\n - "
                            + String.join("\n - ", violations)
                            + "\n\nRemediation:\n"
                            + " - Break the cycle(s) by refactoring dependencies and/or package boundaries.\n"
                            + " - Keep HEXAGONAL family boundaries explicit ("
                            + FAMILY_ADAPTER + "/" + FAMILY_APPLICATION + "/domain/bootstrap) to preserve guardrails integrity."
            );
        }
    }

    private static TreeSet<String> detectContextRoots(JavaClasses classes) {
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

    private static String contextRootForApplicationPackage(String packageName) {
        int idx = packageName.indexOf(APPLICATION_TOKEN);
        if (idx < 0) {
            return BASE_PACKAGE;
        }

        var root = packageName.substring(0, idx);
        return root.isBlank() ? BASE_PACKAGE : root;
    }

    private static String familyToken(String family) {
        return "." + family + ".";
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}