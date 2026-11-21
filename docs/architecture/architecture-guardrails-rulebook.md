# Guardrails rulebook

Use this reference when choosing a mode or investigating a failing generated architecture test. Each rule name below is a generated Java field or method that can appear in a Surefire report. The source of truth is the [governance template tree](../../src/main/resources/templates/springboot/java/governance).

- [What the rules protect](#what-the-rules-protect)
- [Mode selection](#mode-selection)
- [Standard rules](#standard-layered-layout)
- [Hexagonal rules](#hexagonal-ports-and-adapters)
- [Strict MVC boundary](#strict-mvc-transport-boundary)
- [Scope and cycles](#scope-and-cycle-coverage)
- [Reproduce and diagnose](#reproduce-and-diagnose)

## What the rules protect

Dependency rules catch source relationships such as a controller referring to a repository. Boundary rules look at delivery-facing types, for example a domain record exposed through a REST method. Schema rules protect the coverage of the other two: a package rename can leave code compiling while moving classes out of the family selected by a dependency rule. The tests distinguish an empty import, a missing canonical family inside a detected context and a suspicious rename at the base. Each answers a different failure mode.

## Mode selection

| Mode | Generated POM | Generated architecture tests |
| --- | --- | --- |
| `none` | No generated ArchUnit dependency or version property | No ArchUnit sources. The layout and optional sample still build. |
| `basic` (default) | `com.tngtech.archunit:archunit-junit6` in test scope | Layout dependency rules, context-root cycles, scope, schema and anchor checks |
| `strict` | Same test dependency | Every Basic guarantee plus layout-specific restrictions. The equivalent assertion may live in a different test class. MVC adds the REST signature rule. |

The generator chooses the template family using the layout and mode. Strict REST templates come from the `mvc/` group when the effective dependency plan includes MVC. Explicit `web` and the MVC starter added by the basic sample both activate that group. Strict MVC generation fails if the required template group is missing.

[Compare the source dependencies in both layouts](executable-architecture-contract.md#choose-a-layout).

## Standard layered layout

Read arrows as source dependencies. Configuration can wire controllers, services and repositories. Types in services can use domain services, and repositories can use domain models. Package names are relative to the generated base or a recognized feature root.

| Dependency or property | Basic | Strict | Generated assertion or class |
| --- | --- | --- | --- |
| `controller → service → repository` | Permitted | Permitted | Positive sample exercises this path |
| `controller → repository` | Prohibited | Prohibited | `controllers_must_not_depend_on_repositories` |
| `service → controller` | Prohibited | Prohibited | `services_must_not_depend_on_controllers` |
| `repository → controller` | Prohibited | Prohibited | `repositories_must_not_depend_on_controllers` in Basic; combined repository rule in Strict |
| `repository → service` | No direct ban | Prohibited | `repositories_must_not_depend_on_services_or_controllers` |
| `controller → domain.service` | Prohibited | Prohibited | `controllers_must_not_depend_on_domain_services` |
| `domain → controller/service/repository` | Prohibited | Prohibited | Basic `domain_must_not_depend_on_controller_service_or_repository_layers`; Strict `domain_must_depend_only_on_jdk_and_domain` |
| `controller.dto → domain` | No direct ban | Prohibited | `controller_dtos_must_not_depend_on_domain` |
| MVC controller method exposes a domain type in its signature | No signature rule | Prohibited when MVC is enabled | `rest_controllers_must_not_expose_domain_types_in_signatures` |
| Context-root sibling slices | Acyclic | Acyclic | `each_bounded_context_root_must_be_free_of_cycles` |
| Immediate child slices within controller, service, repository, domain and config | No additional slice checks | Acyclic | `StandardStrictPackageCyclesTest` |

Common direction rules live in `StandardBasicArchitectureRulesTest` and `StandardStrictLayerDependencyRulesTest`. Strict permits an ordinary controller to reference a domain model internally for mapping, subject to the separate DTO and REST signature checks. [Standard Basic templates](../../src/main/resources/templates/springboot/java/governance/standard/basic) · [Standard Strict templates](../../src/main/resources/templates/springboot/java/governance/standard/strict)

### Keep presentation out of the service

The fixture introduces a field whose type belongs to `controller`. A type reference alone creates the forbidden bytecode dependency.

```java
package com.example.greeting.service;

class Upward {
    com.example.greeting.controller.Presentation next;
}
```

The Maven build compiles the source, then the named ArchUnit assertion reports the dependency.

```text
StandardStrictLayerDependencyRulesTest.services_must_not_depend_on_controllers
Field <com.example.greeting.service.Upward.next> has type <com.example.greeting.controller.Presentation> in (Upward.java:0)
```

Pass values into the service and map the result at the controller. For context cycles, move wiring into a one-way configuration boundary or extract the shared responsibility. The fixture's `config → service → config` cycle demonstrates why checking only subpackages inside a family leaves a gap.

## Hexagonal ports and adapters

An inbound adapter can call an application port. A use case can call an outbound port, and the concrete outbound adapter implements that port. The source dependency goes from the implementation to the interface. At runtime, the call through the interface reaches the adapter. Bootstrap wires the concrete implementation into the use case.

| Dependency or property | Basic | Strict | Generated assertion or class |
| --- | --- | --- | --- |
| `adapter.in → application.port.in` and `adapter.out → application.port.out` | Permitted | Permitted | Positive generated sample |
| `application → adapter` | Prohibited | Prohibited | `application_must_not_depend_on_adapters` |
| `domain → application/adapter/bootstrap` | Prohibited | Prohibited | `domain_must_not_depend_on_outer_families` in Basic; Strict domain purity encompasses these edges |
| External targets from `domain` | No general framework whitelist | Domain packages and `java.*` | `domain_must_depend_only_on_jdk_and_domain` |
| Non-bootstrap code `→ bootstrap` | Prohibited | Prohibited | `outside_packages_must_not_depend_on_bootstrap` in Basic; `non_bootstrap_packages_must_not_depend_on_bootstrap` in Strict |
| `adapter.in ↔ adapter.out` | No direct ban | Prohibited | `inbound_adapters_must_not_depend_on_outbound_adapters` and `outbound_adapters_must_not_depend_on_inbound_adapters` |
| Adapter `→` non-port application code, including the exact application root | No direct ban | Prohibited | `HexagonalStrictPortsIsolationTest#adapters_must_not_depend_on_application_implementation` |
| Inbound adapter `→ domain.service` or `domain.port.out` | No direct ban | Prohibited | `inbound_adapters_must_not_depend_on_domain_services`, `inbound_adapters_must_not_depend_on_domain_outbound_ports` |
| Inbound DTO package `→ domain` | No direct ban | Prohibited | `inbound_adapter_dtos_must_not_depend_on_domain` |
| MVC inbound adapter method exposes a domain type in its signature | No signature rule | Prohibited when MVC is enabled | `rest_controllers_must_not_expose_domain_types_in_signatures` |
| Context-root sibling slices | Acyclic | Acyclic | `each_bounded_context_root_must_be_free_of_cycles` |
| Child slices of adapter, application, domain, bootstrap and the two adapter directions | No additional slice checks | Acyclic | `HexagonalStrictPackageCyclesTest` |

The Strict port predicate matches the exact application family and accepts the `application.port` subtree. Port APIs can use domain values. Runtime framework wiring belongs in bootstrap. Framework annotations on application code do not violate the domain-only whitelist. [Hexagonal Basic templates](../../src/main/resources/templates/springboot/java/governance/hexagonal/basic) · [Hexagonal Strict templates](../../src/main/resources/templates/springboot/java/governance/hexagonal/strict)

### Depend on a port from the use case

```java
// application.usecase
final class PlaceOrder {
    private final OrderStore store; // application.port.out.OrderStore

    PlaceOrder(OrderStore store) {
        this.store = store;
    }
}
```

Changing the field to the concrete `adapter.out` class produces an architecture violation in both modes. An outbound adapter implementing `OrderStore` is the intended inverse code dependency. The continuity fixture uses a field `Inward.next` to prove the first direction and `Concrete.next` to prove Strict catches adapter dependencies on the exact `application` package.

## Strict MVC transport boundary

Both layouts generate `rest_controllers_must_not_expose_domain_types_in_signatures` when MVC is effective. It observes methods declared by classes directly annotated with `@RestController` in the Standard controller family or Hexagonal inbound adapter family. It checks return and parameter types, including involved generic raw types. The exact `domain` package and descendants are covered.

```java
@RestController
class OrderController {
    // Fails when Order is a domain type. Expose a transport DTO instead.
    ResponseEntity<List<Order>> orders() {
        return ResponseEntity.ok(List.of());
    }
}
```

The rule works on imported bytecode and its declared type information. Composed annotations, other controller styles, inherited routing arrangements and serialized object graphs require separate evidence. Controllers may map domain objects internally; signature checks concern the declared transport surface.

## Scope and cycle coverage

- `guardrails_scope_must_not_be_empty` in `*GuardrailsScopeSanityTest` requires a nonempty import under the generated base package. Imports exclude test classes.
- `*PackageSchemaSanityTest` infers context roots from canonical package segments. Required families are controller/service/domain for Standard and adapter/application/domain for Hexagonal. The assertions are `each_standard_bounded_context_must_contain_required_canonical_families` and `each_hexagonal_bounded_context_must_contain_canonical_families`. A more specific recognized context contributes to its own family check.
- `*GuardrailsAnchorDriftTest` checks suspicious near-miss family names when required base anchors are missing. The rename proof also exercises schema sanity. These checks address different failure modes.
- Both modes use the schema's detected roots plus the project base for sibling-slice checks. At each root, `.(*)..` partitions the next package segment. Strict additionally partitions immediate child segments within its declared families. A one-way dependency between features can pass; a cycle crossing a selected partition fails.
- Some rules allow empty selections so a fresh project with unused optional families still passes. Maven package markers express intended structure. Read the actual imported classes and the report before treating a passing rule as coverage of any particular package or endpoint.

### Read the package vocabulary

A **canonical family** is a package segment used by the emitted rules, such as `controller`, `adapter` or `application`. A **context root** is inferred from production class packages containing recognized family segments. It is an input to schema and slice selection, independent of the team's business definition of a bounded context. A source directory alone cannot tell you which classes ArchUnit discovered.

Required families are designed to cover the core flow, while repository, configuration and bootstrap may be absent from a particular detected context. An **anchor** is a base-level family reference used when checking for near misses. Moving a class between recognized packages changes the selection the rule sees. Review that selection just as you would review an API change and check a representative violation before accepting a revised rule.

These checks intentionally retain the `java.*` namespace policy. A broad `javax.*` exception would admit both JDK and external APIs. Teams extending the rule should test the exact exception they need. The explicit dependency and slice DSL expresses the repository's package policy. ArchUnit's layer and onion DSLs are available when a project's mappings and allowed dependencies fit those models. [ArchUnit guide](https://www.archunit.org/userguide/html/000_Index.html)

## Reproduce and diagnose

Look under `src/test/java/<base-package-path>/architecture/archunit/` for the generated scope, schema and rule sources. Rules run in Surefire's test phase, which Maven reaches on the way to `verify`. Failsafe can exercise the sample HTTP behavior in the same full build.

### Read the failure before changing the rule

| Failure appears in | First question |
| --- | --- |
| A dependency rule | Which source member creates the edge? A constructor parameter, implemented interface or field type can be enough, even when a code path never executes. |
| A context-root cycle | Which sibling slice closes the loop? Read the dependency chain in the report before moving any type. A package rename changes the partition and may leave the dependency in place. |
| A scope or schema assertion | Which production classes did ArchUnit import? Confirm the base package, the first canonical family segment and any more specific context root. A green optional rule can have an empty selection. |
| A Strict REST signature assertion | Which declared parameter or return type exposes the domain? Inspect nested generics as well as top-level types, then map to a transport DTO at the HTTP boundary. |

The test class tells you where to find the selector. The reported member or package tells you where to begin changing the source. For example, a Standard controller holding a repository field can delegate through the service. A Hexagonal inbound adapter holding a concrete handler can depend on its input port, with bootstrap supplying the implementation. If the policy itself has changed, demonstrate a passing baseline and a compiling counterexample against the revised selector.

Generate with `--layout standard` or `--layout hexagonal` and `--guardrails basic` or `strict`. Run `sh ./mvnw clean verify`. Open the named ArchUnit XML in `target/surefire-reports`. Verify the source and target of the failed dependency before changing a package expression or an empty-selection setting. A compiling boundary mutation, a specific assertion failure, and a restored passing build are stronger evidence than a nonzero Maven exit alone. [Executable proof and continuity fixtures](../examples/boundary-proof.md)
