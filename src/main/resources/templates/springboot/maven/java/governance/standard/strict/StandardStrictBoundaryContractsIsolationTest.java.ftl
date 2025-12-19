package ${projectPackageName}.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * Strict boundary contracts isolation for STANDARD (layered) layout.
 * Guarantees:
 * - Controllers must not expose domain types in method signatures (return/params, including generics)
 * - Transport DTOs under controller..dto.. must not depend on domain
 * Notes:
 * - Controllers live under: controller..
 * - Controller DTOs live under: controller..dto..
 * - This intentionally does NOT forbid internal controller package helper classes (e.g. mapper)
 *   from touching domain; we only block domain exposure in controller method signatures.
 */
@AnalyzeClasses(
        packages = "${projectPackageName}",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictBoundaryContractsIsolationTest {

    private static final String BASE_PACKAGE = "${projectPackageName}";

    private static final String DOMAIN_PACKAGE_PATTERN = BASE_PACKAGE + ".domain..";
    private static final String DOMAIN_PREFIX = BASE_PACKAGE + ".domain.";

    private static final String CONTROLLER_PATTERN = BASE_PACKAGE + ".controller..";
    private static final String CONTROLLER_DTO_PATTERN = BASE_PACKAGE + ".controller..dto..";

    @ArchTest
    static final ArchRule controller_dtos_must_not_depend_on_domain =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER_DTO_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(DOMAIN_PACKAGE_PATTERN)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule rest_controllers_must_not_expose_domain_types_in_signatures =
            methods()
                    .that()
                    .areDeclaredInClassesThat()
                    .resideInAnyPackage(CONTROLLER_PATTERN)
                    .and()
                    .areDeclaredInClassesThat()
                    .areAnnotatedWith(RestController.class)
                    .should(notExposeDomainTypesInSignature())
                    .allowEmptyShould(true);

    private static ArchCondition<JavaMethod> notExposeDomainTypesInSignature() {
        return new ArchCondition<>("not expose domain types in controller method signatures") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (String violation : SignatureDomainLeakage.findViolations(method)) {
                    events.add(SimpleConditionEvent.violated(method, violation));
                }
            }
        };
    }

    private static final class SignatureDomainLeakage {

        private SignatureDomainLeakage() {}

        static List<String> findViolations(JavaMethod method) {
            List<String> violations = new ArrayList<>();

            JavaClass rawReturn = method.getRawReturnType();
            if (isDomainType(rawReturn)) {
                violations.add(message(method, "return type leaks domain", rawReturn));
            }

            for (JavaClass p : method.getRawParameterTypes()) {
                if (isDomainType(p)) {
                    violations.add(message(method, "parameter type leaks domain", p));
                }
            }

            if (containsDomainInTypeTree(method.getReturnType())) {
                violations.add(message(method, "generic return type leaks domain", method.getReturnType()));
            }

            for (JavaType pt : method.getParameterTypes()) {
                if (containsDomainInTypeTree(pt)) {
                    violations.add(message(method, "generic parameter type leaks domain", pt));
                }
            }

            return violations;
        }

        private static boolean containsDomainInTypeTree(JavaType type) {
            if (type == null) {
                return false;
            }
            for (JavaClass raw : type.getAllInvolvedRawTypes()) {
                if (isDomainType(raw)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isDomainType(JavaClass c) {
            if (c == null) {
                return false;
            }
            String pkg = c.getPackageName();
            return pkg != null && pkg.startsWith(DOMAIN_PREFIX);
        }

        private static String message(JavaMethod method, String reason, Object type) {
            return reason + ": " + method.getFullName() + " -> " + type;
        }
    }
}