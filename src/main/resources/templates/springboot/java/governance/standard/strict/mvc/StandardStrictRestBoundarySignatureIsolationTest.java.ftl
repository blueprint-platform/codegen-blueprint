package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONTROLLER;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;

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
 * Strict REST boundary signature isolation (STANDARD).
 * Guarantees:
 * - REST controllers must NOT expose domain types in method signatures
 *   (return types, parameters, generics).
 * Notes:
 * - Controller DTO domain isolation is enforced separately.
 * - Works for both flat package roots and nested sub-root structures.
 * Contract note:
 * - Rule scope is the generated application base package.
 * - Pattern helpers are derived locally (rule-scoped, NOT contract).
 */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictRestBoundarySignatureIsolationTest {

    private static final String CONTROLLER_PATTERN = familyPattern(FAMILY_CONTROLLER);
    private static final String BASE_PREFIX = BASE_PACKAGE + ".";

    // Implementation detail for robust detection (NOT contract).
    private static final String DOMAIN_DOTTED_TOKEN = "." + FAMILY_DOMAIN + ".";

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
        return new ArchCondition<>("not expose domain types in REST controller method signatures") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (var violation : SignatureDomainLeakage.findViolations(method)) {
                    events.add(SimpleConditionEvent.violated(method, violation));
                }
            }
        };
    }

    private static final class SignatureDomainLeakage {

        private SignatureDomainLeakage() {}

        static List<String> findViolations(JavaMethod method) {
            var violations = new ArrayList<String>();

            var rawReturn = method.getRawReturnType();
            if (isDomainType(rawReturn)) {
                violations.add(message(method, "return type leaks domain", rawReturn));
            }

            for (var p : method.getRawParameterTypes()) {
                if (isDomainType(p)) {
                    violations.add(message(method, "parameter type leaks domain", p));
                }
            }

            var returnType = method.getReturnType();
            if (containsDomainInTypeTree(returnType)) {
                violations.add(message(method, "generic return type leaks domain", returnType));
            }

            for (var pt : method.getParameterTypes()) {
                if (containsDomainInTypeTree(pt)) {
                    violations.add(message(method, "generic parameter type leaks domain", pt));
                }
            }

            return List.copyOf(violations);
        }

        private static boolean containsDomainInTypeTree(JavaType type) {
            if (type == null) {
                return false;
            }
            for (var raw : type.getAllInvolvedRawTypes()) {
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
            var pkg = c.getPackageName();
            if (pkg == null || pkg.isBlank()) {
                return false;
            }
            return pkg.startsWith(BASE_PREFIX) && pkg.contains(DOMAIN_DOTTED_TOKEN);
        }

        private static String message(JavaMethod method, String reason, Object type) {
            return reason + ": " + method.getFullName() + " -> " + type;
        }
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}