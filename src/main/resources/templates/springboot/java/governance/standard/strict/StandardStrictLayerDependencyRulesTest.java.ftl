package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.DOMAIN_SERVICE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONTROLLER;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_REPOSITORY;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_SERVICE;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict layered dependency rules for STANDARD (layered) architecture.
 * Enforces (build-time, deterministic):
 * - Controllers must NOT depend on repositories
 * - Controllers must NOT depend on domain services
 * - Services must NOT depend on controllers
 * - Repositories must NOT depend on services or controllers
 * Intent:
 * - Controllers act as HTTP / delivery boundary only
 * - Business orchestration belongs to the service layer
 * - Domain services must never be invoked directly from controllers
 * Notes:
 * - Domain models may still be used internally by mappers
 * - REST signature leakage is enforced separately
 * - Works for both flat package roots and nested sub-root structures
 * Contract note:
 * - Rule scope is the generated application base package
 * - Package matchers are fully qualified to avoid accidental matches
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictLayerDependencyRulesTest {

    // ---------------------------------------------------------------------------------------------
    // Rule-scoped patterns (NOT contract)
    // ---------------------------------------------------------------------------------------------

    private static final String CONTROLLER = familyPattern(FAMILY_CONTROLLER);
    private static final String SERVICE = familyPattern(FAMILY_SERVICE);
    private static final String REPOSITORY = familyPattern(FAMILY_REPOSITORY);
    private static final String DOMAIN_SERVICE_PATTERN = domainServicePattern();

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_repositories =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(REPOSITORY)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_domain_services =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(DOMAIN_SERVICE_PATTERN)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers =
            noClasses()
                    .that()
                    .resideInAnyPackage(SERVICE)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(CONTROLLER)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_services_or_controllers =
            noClasses()
                    .that()
                    .resideInAnyPackage(REPOSITORY)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(SERVICE, CONTROLLER)
                    .allowEmptyShould(true);

    // ---------------------------------------------------------------------------------------------
    // Local pattern helpers (rule-scoped, NOT contract)
    // ---------------------------------------------------------------------------------------------

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }

    private static String domainServicePattern() {
        return BASE_PACKAGE + ".." + FAMILY_DOMAIN + "." + DOMAIN_SERVICE + "..";
    }
}