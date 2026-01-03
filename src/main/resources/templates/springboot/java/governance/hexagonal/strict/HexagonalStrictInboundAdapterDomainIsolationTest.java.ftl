package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_IN;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.DOMAIN_SERVICE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict inbound adapter → domain isolation (HEXAGONAL).
 * Guarantees:
 * - Inbound adapters must not depend on domain services
 * Notes:
 * - Domain models may still be used by inbound adapters if explicitly allowed
 * - REST method signature leakage is enforced by a separate rule
 * - Works for both flat package roots and nested sub-root structures
 * Contract note:
 * - Rule scope is the generated application base package
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictInboundAdapterDomainIsolationTest {

    private static final String ADAPTER_IN_PATTERN = familySubPattern(FAMILY_ADAPTER, ADAPTER_IN);
    private static final String DOMAIN_SERVICE_PATTERN = familySubPattern(FAMILY_DOMAIN, DOMAIN_SERVICE);

    @ArchTest
    static final ArchRule inbound_adapters_must_not_depend_on_domain_services =
            noClasses()
                    .that()
                    .resideInAnyPackage(ADAPTER_IN_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(DOMAIN_SERVICE_PATTERN)
                    .allowEmptyShould(true);

    private static String familySubPattern(String family, String sub) {
        return BASE_PACKAGE + ".." + family + "." + sub + "..";
    }
}