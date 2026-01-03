package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_DTO;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_IN;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict inbound DTO isolation (HEXAGONAL).
 * Guarantees:
 * - Inbound adapter DTOs must not depend on domain types
 * Notes:
 * - DTOs represent boundary data structures and remain framework-facing only
 * - Works for both flat package roots and nested sub-root structures
 * Contract note:
 * - Rule scope is the generated application base package
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictInboundDtoDomainIsolationTest {

    private static final String ADAPTER_IN_DTO_PATTERN =
            familyNestedSubPattern(FAMILY_ADAPTER, ADAPTER_IN, ADAPTER_DTO);

    private static final String DOMAIN_PATTERN =
            familyPattern(FAMILY_DOMAIN);

    @ArchTest
    static final ArchRule inbound_adapter_dtos_must_not_depend_on_domain =
            noClasses()
                    .that()
                    .resideInAnyPackage(ADAPTER_IN_DTO_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(DOMAIN_PATTERN)
                    .allowEmptyShould(true);

    private static String familyNestedSubPattern(String family, String parent, String child) {
        return BASE_PACKAGE + ".." + family + "." + parent + ".." + child + "..";
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}