# Contributing

Use [issues](https://github.com/blueprint-platform/codegen-blueprint/issues) for reproducible defects and focused proposals. Include the generator version, generator and target JDKs, selectors, resolved Boot patch and source, and the first relevant failure. Use the [security policy](SECURITY.md) for private disclosures.

## Verify a change

Build and run the generator with JDK 25 and Maven 3.9.16. Generated projects can target Java 21 or 25.

```bash
mvn -B -ntp clean verify
```

This runs generator unit, architecture and integration tests. For changes to generated output, run the affected [Standard or Full matrix](docs/maintainers/release-discipline.md#verification-levels) from the repository root with a matching target JDK. Run the [negative proof](docs/examples/boundary-proof.md) when modifying guardrails or build wiring.

For documentation changes, run the Pages and quickstart checks described in the [documentation tooling guide](docs/maintainers/documentation.md). The quickstart check executes the fenced commands from the user-facing guide. See [visual assets](docs/maintainers/visual-assets.md) for source files and proof provenance.

## Submit a focused change

- Describe the intent, owned boundary, observable behavior and proof. Follow the [architecture governance protocol](docs/maintainers/architecture-governance-and-ai-protocol.md).
- Preserve domain independence, deterministic artifact order and the [generation contract](docs/architecture/executable-architecture-contract.md).
- Change templates or generation models for product output. Regenerate tracked examples with the packaged JAR.
- State which environments and evidence you verified. Separate generator tests from generated builds and local results from hosted checks.

Contributions use the [MIT license](LICENSE).
