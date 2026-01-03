package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Strict domain purity (STANDARD).
 * Guarantees:
 * - Domain depends only on JDK types
 * - Domain depends only on other domain types
 * Notes:
 * - Works for both flat package roots and nested sub-root structures
 * Contract note:
 * - Domain is treated as a pure, framework-free core (within the layered model)
 * - Rule scope is the generated application base package
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictDomainPurityTest {

    private static final String DOMAIN_PATTERN = familyPattern(FAMILY_DOMAIN);
    private static final String BASE_PREFIX = BASE_PACKAGE + ".";

    // Implementation detail for robust detection (NOT contract).
    private static final String DOMAIN_DOTTED_TOKEN = "." + FAMILY_DOMAIN + ".";

    @ArchTest
    static final ArchRule domain_must_depend_only_on_jdk_and_domain =
            noClasses()
                    .that()
                    .resideInAnyPackage(DOMAIN_PATTERN)
                    .should()
                    .dependOnClassesThat(
                            describe(
                                    "reside outside domain and are not JDK types",
                                    c -> !isAllowedForDomain(c)
                            )
                    )
                    .allowEmptyShould(true);

    private static boolean isAllowedForDomain(JavaClass c) {
        var pkg = c.getPackageName();
        if (pkg == null || pkg.isBlank()) {
            return true;
        }
        if (pkg.startsWith("java.")) {
            return true;
        }
        return isUnderBasePackage(pkg) && isDomainType(pkg);
    }

    private static boolean isUnderBasePackage(String packageName) {
        return packageName.startsWith(BASE_PREFIX);
    }

    private static boolean isDomainType(String packageName) {
        return packageName.contains(DOMAIN_DOTTED_TOKEN);
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}