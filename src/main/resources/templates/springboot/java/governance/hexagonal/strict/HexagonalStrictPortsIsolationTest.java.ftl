package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.APPLICATION_PORT;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_APPLICATION;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict ports & adapters isolation (HEXAGONAL).
 * Guarantees:
 * - Adapters may depend on application ports
 * - Adapters must not depend on application implementation classes
 *   (use cases, services, or any non-port application types)
 * Notes:
 * - This rule enforces the core hexagonal dependency direction
 * - Structural by design and independent of package root shape
 * - Works for both flat package roots and nested sub-root structures
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalStrictPortsIsolationTest {

    private static final String APPLICATION_TOKEN = familyToken(FAMILY_APPLICATION);
    private static final String PORT_TOKEN = "." + APPLICATION_PORT + ".";

    @ArchTest
    static final ArchRule adapters_must_not_depend_on_application_implementation =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_ADAPTER))
                    .should()
                    .dependOnClassesThat(
                            describe(
                                    "reside in application but are not ports (outside '." + APPLICATION_PORT + ".' )",
                                    HexagonalStrictPortsIsolationTest::isApplicationImplementationType
                            )
                    )
                    .allowEmptyShould(true);

    private static boolean isApplicationImplementationType(JavaClass c) {
        var pkg = c.getPackageName();
        if (pkg == null || pkg.isBlank()) {
            return false;
        }

        return pkg.startsWith(BASE_PACKAGE + ".")
                && pkg.contains(APPLICATION_TOKEN)
                && !pkg.contains(PORT_TOKEN);
    }

    private static String familyToken(String family) {
        return "." + family + ".";
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}