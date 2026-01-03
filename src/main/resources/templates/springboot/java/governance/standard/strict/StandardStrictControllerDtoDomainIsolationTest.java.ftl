package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.CONTROLLER_DTO;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONTROLLER;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict controller DTO isolation (STANDARD).
 * <h2>Guarantee</h2>
 * Controller DTOs must not depend on domain types.
 * <h2>Notes</h2>
 * <ul>
 *   <li>Works for both flat roots and nested bounded contexts.</li>
 *   <li>Match patterns are derived from the canonical contract ({@link StandardGuardrailsScope}).</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictControllerDtoDomainIsolationTest {

    @ArchTest
    static final ArchRule controller_dtos_must_not_depend_on_domain =
            noClasses()
                    .that()
                    .resideInAnyPackage(controllerDtoPattern())
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(domainFamilyPattern())
                    .allowEmptyShould(true);

    // ---------------------------------------------------------------------------------------------
    // Local pattern helpers (rule-scoped, NOT contract)
    // ---------------------------------------------------------------------------------------------

    private static String controllerDtoPattern() {
        return BASE_PACKAGE + ".." + FAMILY_CONTROLLER + "." + CONTROLLER_DTO + "..";
    }

    private static String domainFamilyPattern() {
        return BASE_PACKAGE + ".." + FAMILY_DOMAIN + "..";
    }
}