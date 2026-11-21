# Documentation tooling

The Pages site builds from the repository’s Markdown source. Use the [documentation index](../index.md) to navigate the active reference pages, generated walkthroughs and linked assets.

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

For visual review, open the Pages routes and the full README at desktop and 375 px widths in light and dark mode. Open the mobile navigation with the keyboard and check that it exposes the same document groups as the desktop sidebar. Check command wrapping, long assertion names and image readability. The Markdown link check covers repository documents as well as Pages inputs, and the rendered site check covers copied assets and HTML anchors.
