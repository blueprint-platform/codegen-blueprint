package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_BOOTSTRAP;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict bootstrap leaf enforcement (HEXAGONAL).
 * <h2>Guarantee</h2>
 * <ul>
 *   <li>The {@code bootstrap} package is a <b>strict leaf</b>.</li>
 *   <li>No non-bootstrap package may depend on {@code bootstrap}.</li>
 * </ul>
 * <h2>Why this matters</h2>
 * <ul>
 *   <li>{@code bootstrap} is the composition root.</li>
 *   <li>It wires the application but must not be referenced by it.</li>
 *   <li>Allowing dependencies toward bootstrap creates hidden runtime coupling.</li>
 * </ul>
 * <h2>Contract intent</h2>
 * <ul>
 *   <li>This rule makes bootstrap isolation <b>non-negotiable</b> in strict mode.</li>
 *   <li>Violations indicate an architectural inversion, not a cosmetic issue.</li>
 *   <li>This rule complements (but is independent from) cycle detection.</li>
 * </ul>
 * <h2>Notes</h2>
 * <ul>
 *   <li>Works for both flat package roots and nested bounded contexts.</li>
 *   <li>Canonical family names are defined in {@link HexagonalGuardrailsScope}.</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictBootstrapLeafTest {

    /**
     * Canonical package pattern for the bootstrap family.
     * Defined once to keep rule intent explicit and avoid pattern drift.
     */
    private static final String BOOTSTRAP_PATTERN =
            BASE_PACKAGE + ".." + FAMILY_BOOTSTRAP + "..";

    @ArchTest
    static final ArchRule non_bootstrap_packages_must_not_depend_on_bootstrap =
            noClasses()
                    .that()
                    .resideOutsideOfPackage(BOOTSTRAP_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(BOOTSTRAP_PATTERN)
                    .allowEmptyShould(true);
}