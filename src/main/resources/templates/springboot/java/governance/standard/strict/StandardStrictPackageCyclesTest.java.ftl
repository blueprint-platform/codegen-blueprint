package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONFIG;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONTROLLER;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_REPOSITORY;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_SERVICE;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict package cycle rules (STANDARD).
 * Guarantees:
 * - No cyclic dependencies inside each layered package family
 *   (controller/service/repository/domain/config).
 * Notes:
 * - Works for both flat package roots and nested sub-root structures.
 * - Empty projects are allowed (no false negatives).
 * Contract note:
 * - Rule scope is the generated application base package.
 * - Slice matchers are derived locally (rule-scoped, NOT contract).
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictPackageCyclesTest {

    // ---------------------------------------------------------------------------------------------
    // Rule-scoped slice patterns (NOT contract)
    // ---------------------------------------------------------------------------------------------

    private static final String CONTROLLER_SLICE = familySlicePattern(FAMILY_CONTROLLER);
    private static final String SERVICE_SLICE = familySlicePattern(FAMILY_SERVICE);
    private static final String REPOSITORY_SLICE = familySlicePattern(FAMILY_REPOSITORY);
    private static final String DOMAIN_SLICE = familySlicePattern(FAMILY_DOMAIN);
    private static final String CONFIG_SLICE = familySlicePattern(FAMILY_CONFIG);

    @ArchTest
    static final ArchRule controller_packages_must_be_free_of_cycles =
            slices()
                    .matching(CONTROLLER_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule service_packages_must_be_free_of_cycles =
            slices()
                    .matching(SERVICE_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repository_packages_must_be_free_of_cycles =
            slices()
                    .matching(REPOSITORY_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_packages_must_be_free_of_cycles =
            slices()
                    .matching(DOMAIN_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule config_packages_must_be_free_of_cycles =
            slices()
                    .matching(CONFIG_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    private static String familySlicePattern(String family) {
        return BASE_PACKAGE + ".." + family + ".(*)..";
    }
}