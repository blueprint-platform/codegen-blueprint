# Generated project walkthroughs

These are **generated README snapshots**, not independently maintained tutorials. They show the 2.0 snapshot CLI's output for Spring Boot 4.2, Java 25, Strict guardrails and the basic sample.

| Layout | Snapshot |
| --- | --- |
| Standard (layered) | [Standard project README](standard.md) |
| Hexagonal (ports and adapters) | [Hexagonal project README](hexagonal.md) |

## Follow one greeting through both designs

Both snapshots describe the same behavior with two dependency structures. In **Standard**, start with `GreetingController`, follow `GreetingService` to the domain service, and find the audit side effect behind the repository. In **Hexagonal**, find `GetGreetingPort` at the request boundary and follow the implementation into the domain and output port. The use case and the concrete output adapter both reference the domain-owned output interface.

| Responsibility | Standard | Hexagonal |
| --- | --- | --- |
| HTTP entry | `controller.GreetingController` | `adapter.in.rest.GreetingController` |
| Orchestration called by the entry point | `service.GreetingService` | `application.port.in.GetGreetingPort`, implemented by `GetGreetingHandler` |
| Audit boundary used by orchestration | `repository.GreetingAuditRepository` | `domain.port.out.GreetingAuditPort` |
| Concrete assembly | `config.GreetingSampleConfig` | `bootstrap.GreetingSampleConfig` |

The generated documentation lets the recipient identify the exact stack, locate the rules, run the sample and inspect the HTTP endpoints without referring back to the generator. To see an assertion reject a change to the delivered sources, use the [boundary proof](boundary-proof.md). To follow a request inside the generator, use the [engine walkthrough](../guides/how-to-explore-hexagonal-architecture.md).

The snapshots intentionally preserve the generator's current formatting. To change their content, update the generation templates and follow the [snapshot verification and regeneration instructions](../maintainers/documentation.md), rather than editing these files manually.
