package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.ADAPTER_IN;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.DOMAIN_PORT;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.DOMAIN_PORT_OUT;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict inbound adapter → domain outbound port isolation (HEXAGONAL).
 * Guarantees:
 * - Inbound adapters must not depend on domain outbound ports
 * Notes:
 * - This rule enforces correct direction of control flow
 * - Works for both flat package roots and nested sub-root structures
 * Contract note:
 * - Rule scope is the generated application base package
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictInboundAdapterOutboundPortIsolationTest {

    private static final String ADAPTER_IN_PATTERN =
            familySubPattern(FAMILY_ADAPTER, ADAPTER_IN);

    private static final String DOMAIN_OUTBOUND_PORT_PATTERN =
            familyNestedSubPattern(FAMILY_DOMAIN, DOMAIN_PORT, DOMAIN_PORT_OUT);

    @ArchTest
    static final ArchRule inbound_adapters_must_not_depend_on_domain_outbound_ports =
            noClasses()
                    .that()
                    .resideInAnyPackage(ADAPTER_IN_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(DOMAIN_OUTBOUND_PORT_PATTERN)
                    .allowEmptyShould(true);

    private static String familySubPattern(String family, String sub) {
        return BASE_PACKAGE + ".." + family + "." + sub + "..";
    }

    private static String familyNestedSubPattern(String family, String parent, String child) {
        return BASE_PACKAGE + ".." + family + "." + parent + "." + child + "..";
    }
}