package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_IN;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_OUT;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_APPLICATION;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_BOOTSTRAP;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict package cycle rules (HEXAGONAL).
 * Guarantees:
 * - No cyclic dependencies inside adapter packages
 * - No cyclic dependencies inside application packages
 * - No cyclic dependencies inside domain packages
 * - No cyclic dependencies inside bootstrap packages
 * - No cyclic dependencies inside adapter.in subpackages
 * - No cyclic dependencies inside adapter.out subpackages
 * Notes:
 * - Rules are applied per hexagonal family to prevent local cycles
 *   without enforcing unnecessary global coupling constraints
 * - Works for both flat package roots and nested sub-root structures
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictPackageCyclesTest {

    private static final String ADAPTER_SLICE = familySlicePattern(FAMILY_ADAPTER);
    private static final String APPLICATION_SLICE = familySlicePattern(FAMILY_APPLICATION);
    private static final String DOMAIN_SLICE = familySlicePattern(FAMILY_DOMAIN);
    private static final String BOOTSTRAP_SLICE = familySlicePattern(FAMILY_BOOTSTRAP);

    private static final String ADAPTER_IN_SLICE = adapterSubSlicePattern(ADAPTER_IN);
    private static final String ADAPTER_OUT_SLICE = adapterSubSlicePattern(ADAPTER_OUT);

    @ArchTest
    static final ArchRule adapter_packages_must_be_free_of_cycles =
            slices()
                    .matching(ADAPTER_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule application_packages_must_be_free_of_cycles =
            slices()
                    .matching(APPLICATION_SLICE)
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
    static final ArchRule bootstrap_packages_must_be_free_of_cycles =
            slices()
                    .matching(BOOTSTRAP_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule adapter_in_packages_must_be_free_of_cycles =
            slices()
                    .matching(ADAPTER_IN_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule adapter_out_packages_must_be_free_of_cycles =
            slices()
                    .matching(ADAPTER_OUT_SLICE)
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    private static String familySlicePattern(String family) {
        return BASE_PACKAGE + ".." + family + ".(*)..";
    }

    private static String adapterSubSlicePattern(String subFamily) {
        return BASE_PACKAGE + ".." + FAMILY_ADAPTER + "." + subFamily + ".(*)..";
    }
}