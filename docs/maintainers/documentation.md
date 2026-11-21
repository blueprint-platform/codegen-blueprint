# Documentation tooling

The Pages site renders the same Markdown files used by GitHub. The [documentation index](../index.md) defines the reader routes. The renderer includes the active reference pages and their linked assets. Historical material remains in the tagged release.

From the repository root with Python 3.12:

```bash
python3 -m venv target/docs-env
target/docs-env/bin/python -m pip install --require-hashes -r scripts/pages/requirements.txt
target/docs-env/bin/python scripts/build-docs.py target/pages
python3 -m http.server --directory target/pages 8000
```

Pages links are relative, so the same output works at a project subpath. The build checks active local links and rendered anchors. Repository-only links are directed to GitHub. Deployment is a separate, explicitly requested workflow job restricted to `main`.

To check the documented commands with JDK 25 and a packaged JAR:

```bash
python3 scripts/verify-quickstart.py target/codegen-blueprint-2.0.0-SNAPSHOT.jar
```

Build & Test reuses the Java 25 engine artifact on JDK 25 and executes the fenced block from the [quickstart](../quickstart.md). CI also compares the generated walkthrough snapshots with fresh output from the same JAR. The executable proof is separate and checks the named ArchUnit failure and restored build.
