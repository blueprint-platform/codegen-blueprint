package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict package cycle rules (HEXAGONAL).
 * Guarantees:
 * - No cycles across top-level packages (under base package)
 * - No cycles inside adapter subpackages
 * Notes:
 * - Rule scope is the generated base package.
 * - For empty or minimal projects, allowEmptyShould(true) prevents false failures.
 */
@AnalyzeClasses(
        packages = HexagonalStrictPackageCyclesTest.BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictPackageCyclesTest {

    static final String BASE_PACKAGE = "${projectPackageName}";

    @ArchTest
    static final ArchRule top_level_packages_must_be_free_of_cycles =
            slices()
                    .matching(BASE_PACKAGE + ".(*)..")
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule adapter_subpackages_must_be_free_of_cycles =
            slices()
                    .matching(BASE_PACKAGE + ".adapter.(*)..")
                    .should()
                    .beFreeOfCycles()
                    .allowEmptyShould(true);
}