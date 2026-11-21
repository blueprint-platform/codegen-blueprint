# Documentation tooling

The Pages site renders the same Markdown files used by GitHub. The [documentation index](../index.md) defines the reader routes. The site includes the active reference pages, both generated walkthroughs and their linked assets.

From the repository root with Python 3.12:

```bash
python3 -m venv target/docs-env
target/docs-env/bin/python -m pip install --require-hashes -r scripts/pages/requirements.txt
target/docs-env/bin/python scripts/build-docs.py target/pages
python3 -m http.server --directory target/pages 8000
```

Pages links are relative, so the same output works at a project subpath. The build checks active local links and rendered anchors. Repository-only links open the matching source revision on GitHub in CI previews. Local builds default to the canonical repository’s `main` branch. Override that with `DOCS_SOURCE_REPOSITORY` (owner/repository) and `DOCS_SOURCE_REF` (branch or SHA). Deployment is a separate, explicitly requested workflow job restricted to `main`.

To check the documented commands with JDK 25 and a packaged JAR:

```bash
python3 scripts/verify-quickstart.py target/codegen-blueprint-2.0.0-SNAPSHOT.jar
```

Build & Test reuses the Java 25 engine artifact on JDK 25 and executes the fenced block from the [quickstart](../quickstart.md). CI also compares the generated walkthrough snapshots with fresh output from the same JAR. Check or regenerate the walkthroughs with the same packaged JAR.

```bash
bash scripts/refresh-generated-readmes.sh target/codegen-blueprint-2.0.0-SNAPSHOT.jar --check
```

Omit `--check` to refresh the tracked snapshots. The snapshot script uses the bundled Boot patch for repeatable output. The [executable proof](../examples/boundary-proof.md) separately checks the named ArchUnit failure and restored build.
