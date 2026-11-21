# Explore the generator's Hexagonal Architecture

Trace one request through Codegen Blueprint, then compare the engine's boundaries with the Hexagonal project it produces. Both use ports to make ownership visible in Java dependencies. The engine and the generated project have separate rule sets and lifecycles.

## Follow a CLI request to the application boundary

Start with [SpringBootGenerateCommand](../../src/main/java/io/github/blueprintplatform/codegen/adapter/in/cli/springboot/SpringBootGenerateCommand.java). It accepts Picocli options, converts dependency aliases and passes an application command through [CreateProjectCommandMapper](../../src/main/java/io/github/blueprintplatform/codegen/adapter/in/cli/mapper/CreateProjectCommandMapper.java). [CreateProjectPort](../../src/main/java/io/github/blueprintplatform/codegen/application/port/in/project/CreateProjectPort.java) exposes the use case to the CLI in terms of the command and result types it owns.

The key source dependency is from the command adapter to the input port. A parser feature such as an option alias belongs to the CLI. A blueprint invariant such as whether the Java target is supported belongs to the domain. Keeping those decisions at their owning boundaries lets input parsing and request validation be tested independently.

## Read the use case in execution order

[CreateProjectHandler](../../src/main/java/io/github/blueprintplatform/codegen/application/usecase/project/CreateProjectHandler.java) follows this sequence:

1. Validate the command with domain value objects and require a target directory value.
2. Select the artifact profile by stack and validate profile-specific metadata. For Maven, the project name and description must be valid literal XML text.
3. Resolve the target through `ProjectTargetResolver` and create a `ProjectBlueprint` containing the resolved target.
4. Ask the selected `ProjectArtifactsPort` to produce ordered resources.
5. Pass the resources to `ProjectPublisherPort` and map the published paths and resolution source into the result.

The order determines observable failure precedence. Invalid metadata fails before Central access. Filesystem publication follows rendering. The use case needs neither Spring's HTTP request factory nor the filesystem classes that claim output paths.

## Place ports where the decision lives

| Boundary | Example | Responsibility |
| --- | --- | --- |
| Domain | `ProjectBlueprint`, `SpringBootJvmTarget`, generated resource values | Express valid generation intent, compatibility and the shape of a resource. |
| Application input | `CreateProjectPort` | Define what a delivery adapter can ask the engine to do. |
| Application output | `ProjectTargetResolver`, `ProjectArtifactsSelector`, `ProjectArtifactsPort`, `ProjectPublisherPort` | Describe operations needed to complete the use case. |
| Adapters | Spring Boot patch lookup, Maven profile, filesystem publisher | Implement provider, template and filesystem decisions. |
| Bootstrap | Profile registry and Spring wiring | Bind concrete implementations to ports and check the artifact pipeline at startup. |

The generated resource model represents paths, directories and file content before publication. The use case treats delivery as one operation through the publisher port. The filesystem publisher coordinates writing, archiving and listing behind that boundary. This keeps ownership of rollback and exclusive creation together. [Publisher source](../../src/main/java/io/github/blueprintplatform/codegen/adapter/out/filesystem/FileSystemProjectPublisher.java)

## See how the profile materializes the contract

The `spring-boot-maven-java` profile lists artifact keys in execution order in [application.yml](../../src/main/resources/application.yml). Bootstrap checks the configured keys, bindings and fixed template definitions. The Maven profile computes an effective dependency plan for the POM and governance artifacts. That shared decision means a sample that adds MVC also activates the Strict REST template group.

Read the pipeline as build configuration and wrapper → source layout and entrypoints → governance and sample → README. Each artifact handles its own applicability and template model. Optional sample or guardrail files can be omitted without changing the other artifacts. Rendering syntax lives in FreeMarker, and publication receives the resulting resource list.

## Follow the same idea in the generated sample

Run the [quickstart](../quickstart.md) and follow `bp.sample.adapter.in.rest.GreetingController` into `GetGreetingPort` and its use-case implementation. The controller depends on the application-owned input interface. The handler uses the domain service and `domain.port.out.GreetingAuditPort`, and the logging adapter implements that port. The sample places its audit contract in the domain. The generator places its resolver and publisher contracts in the application because those operations serve generation and delivery. Bootstrap has access to both sides because it assembles the objects. Read source arrows toward the interfaces even when a runtime call eventually reaches an adapter. [Generated Hexagonal walkthrough](../examples/hexagonal.md)

A controller that takes the concrete handler can compile and serve the same request. It couples delivery to application implementation and fails `adapters_must_not_depend_on_application_implementation` in Strict mode. The proof demonstrates this with a field declaration, and separately tests that Basic and Strict preserve inward application dependency direction. [Boundary proof](../examples/boundary-proof.md)

## Read the tests at the layer they verify

Run `mvn -B -ntp test` in the repository to see engine tests and ArchUnit. Use `mvn -B -ntp clean verify` for the engine's integration tests and executable JAR. The CLI integration test invokes the executor in-process. For output evidence, build a fresh generated project with its own wrapper and inspect the Surefire and Failsafe reports. [Compatibility and verification coverage](../compatibility.md#verification-coverage)
