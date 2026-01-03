package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_IN;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_OUT;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict adapter direction rules (HEXAGONAL).
 * Guarantees:
 * - Inbound adapters do not depend on outbound adapters.
 * - Outbound adapters do not depend on inbound adapters.
 * Notes:
 * - Works for both flat package roots and nested sub-root structures.
 * Contract note:
 * - Rule scope is the generated application base package.
 * - Canonical family names are defined in {@link HexagonalGuardrailsScope}.
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictAdapterDirectionRulesTest {

    private static final String ADAPTER_IN_PATTERN = familySubPattern(FAMILY_ADAPTER, ADAPTER_IN);
    private static final String ADAPTER_OUT_PATTERN = familySubPattern(FAMILY_ADAPTER, ADAPTER_OUT);

    @ArchTest
    static final ArchRule inbound_adapters_must_not_depend_on_outbound_adapters =
            noClasses()
                    .that()
                    .resideInAnyPackage(ADAPTER_IN_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(ADAPTER_OUT_PATTERN)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule outbound_adapters_must_not_depend_on_inbound_adapters =
            noClasses()
                    .that()
                    .resideInAnyPackage(ADAPTER_OUT_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(ADAPTER_IN_PATTERN)
                    .allowEmptyShould(true);

    private static String familySubPattern(String family, String sub) {
        return BASE_PACKAGE + ".." + family + "." + sub + "..";
    }
}