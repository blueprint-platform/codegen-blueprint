#!/usr/bin/env python3
"""Build Pages from the same Markdown files used by GitHub and check local links."""

import argparse
from html import escape
from html.parser import HTMLParser
import os
import re
from pathlib import Path
import shutil
from urllib.parse import quote, unquote, urlsplit

import markdown
from markdown.extensions import Extension
from markdown.treeprocessors import Treeprocessor

ROOT = Path(__file__).resolve().parents[1]
STYLESHEET = "site.css"
GITHUB = "https://github.com/blueprint-platform/codegen-blueprint"
SOURCE_REPO = os.environ.get("DOCS_SOURCE_REPOSITORY", "blueprint-platform/codegen-blueprint")
SOURCE_REF = os.environ.get("DOCS_SOURCE_REF", "main")
NAV = {
    "index": "Overview",
    "examples/boundary-proof": "Proof",
    "quickstart": "Quickstart",
    "cli": "CLI",
    "architecture/executable-architecture-contract": "Architecture",
}
PAGES = {
    **NAV,
    "compatibility": "Compatibility",
    "examples/generated-projects": "Examples",
    "examples/standard": "Standard example",
    "examples/hexagonal": "Hexagonal example",
    "architecture/architecture-guardrails-rulebook": "Guardrails",
    "release-notes": "Release notes",
    "guides/how-to-explore-hexagonal-architecture": "Generator architecture",
    "policies/scope-and-responsibility": "Scope and responsibility",
    "maintainers/architecture-governance-and-ai-protocol": "Architecture governance",
    "maintainers/documentation": "Documentation tooling",
    "maintainers/release-discipline": "Release readiness",
    "maintainers/visual-assets": "Visual assets",
}
SECTIONS = {
    "Build a project": (
        "quickstart", "cli", "examples/generated-projects",
        "examples/standard", "examples/hexagonal",
    ),
    "Evaluate the architecture": (
        "examples/boundary-proof", "architecture/executable-architecture-contract",
        "architecture/architecture-guardrails-rulebook", "guides/how-to-explore-hexagonal-architecture",
        "policies/scope-and-responsibility",
    ),
    "Verify and maintain": (
        "compatibility", "release-notes", "maintainers/release-discipline",
        "maintainers/architecture-governance-and-ai-protocol", "maintainers/documentation",
        "maintainers/visual-assets",
    ),
}
ROUTES = {ROOT / "docs" / f"{name}.md": Path(f"{name}.html") for name in PAGES}


class Links(Treeprocessor):
    def __init__(self, source, route, assets):
        self.source, self.route, self.assets = source, route, assets

    def run(self, tree):
        for element in tree.iter():
            for attr in ("href", "src"):
                raw = element.get(attr)
                if not raw:
                    continue
                rewritten = self.rewrite_link(raw, element.tag)
                if rewritten is not None:
                    element.set(attr, rewritten)

    def rewrite_link(self, raw, tag):
        url = urlsplit(raw)
        if url.scheme or url.netloc or not url.path:
            return None
        target = (self.source.parent / unquote(url.path)).resolve()
        if not target.is_relative_to(ROOT) or not target.exists():
            raise ValueError(f"{self.source.relative_to(ROOT)}: missing local target {raw}")
        if tag == "img" and not target.is_file():
            raise ValueError(f"{self.source.relative_to(ROOT)}: image is not a file {raw}")

        fragment = f"#{url.fragment}" if url.fragment else ""
        if target in ROUTES:
            destination = ROUTES[target]
        elif tag == "img" or target.suffix in {".txt", ".svg", ".png"}:
            destination = target.relative_to(ROOT / "docs")
            self.assets.add(destination)
        else:
            view = "tree" if target.is_dir() else "blob"
            return (f"https://github.com/{quote(SOURCE_REPO)}/{view}/{quote(SOURCE_REF)}"
                    f"/{quote(target.relative_to(ROOT).as_posix())}{fragment}")
        return quote(os.path.relpath(destination, self.route.parent)) + fragment


class LocalLinks(Extension):
    def __init__(self, source, route, assets):
        self.processor = Links(source, route, assets)

    def extendMarkdown(self, renderer):
        renderer.treeprocessors.register(self.processor, "local_links", 1)


def heading_slug(value, separator):
    # Keep the punctuation and spacing behavior of GitHub's Markdown anchors,
    # including leading hyphens after emoji in the generated README headings.
    return re.sub(r"[^\w\- ]", "", value.lower()).replace(" ", separator)


def render_document(source, route, assets):
    extensions = ["fenced_code", "tables", "toc"]
    if route is not None:
        extensions.append(LocalLinks(source, route, assets))
    document = markdown.Markdown(
        extensions=extensions,
        extension_configs={"toc": {"slugify": heading_slug}},
        output_format="html",
    )
    html = document.convert(source.read_text(encoding="utf-8"))
    return html, document.toc_tokens


def render(source, route, assets):
    return render_document(source, route, assets)[0]


def page_outline(tokens):
    sections = [child for heading in tokens if heading["level"] == 1
                for child in heading["children"] if child["level"] == 2]
    if len(sections) < 3:
        return ""
    links = "".join(
        f'<li><a href="#{escape(section["id"], quote=True)}">'
        f'{escape(section["name"])}</a></li>'
        for section in sections
    )
    return f'<nav class="page-outline" aria-label="On this page"><p>On this page</p><ul>{links}</ul></nav>'


class HtmlLinks(HTMLParser):
    def __init__(self):
        super().__init__()
        self.ids = set()
        self.links = []

    def handle_starttag(self, tag, attrs):
        for name, value in attrs:
            if name == "id":
                self.ids.add(value)
            if name in {"href", "src"} and value:
                url = urlsplit(value)
                if not url.scheme and not url.netloc:
                    self.links.append(url)


def check_site(site):
    pages = {}
    for page in site.rglob("*.html"):
        inspected = HtmlLinks()
        inspected.feed(page.read_text(encoding="utf-8"))
        pages[page] = inspected
    for page, inspected in pages.items():
        for url in inspected.links:
            target = (page.parent / unquote(url.path)).resolve() if url.path else page
            if not target.is_relative_to(site) or not target.is_file():
                raise ValueError(f"{page.name}: missing rendered target {url.geturl()}")
            if url.fragment and target in pages and unquote(url.fragment) not in pages[target].ids:
                raise ValueError(f"{page.name}: missing rendered anchor {url.geturl()}")


def check_markdown():
    rendered = {}
    for source in (*ROOT.glob("*.md"), *(ROOT / ".github").rglob("*.md"), *(ROOT / "docs").rglob("*.md")):
        inspected = HtmlLinks()
        inspected.feed(render(source, None, None))
        rendered[source] = inspected
    for source, inspected in rendered.items():
        for url in inspected.links:
            target = (source.parent / unquote(url.path)).resolve() if url.path else source
            if not target.is_relative_to(ROOT) or not target.exists():
                raise ValueError(f"{source.relative_to(ROOT)}: missing local target {url.geturl()}")
            if url.fragment and target in rendered and unquote(url.fragment) not in rendered[target].ids:
                raise ValueError(f"{source.relative_to(ROOT)}: missing Markdown anchor {url.geturl()}")


def build(site):
    assets = set()
    check_markdown()
    for source, route in ROUTES.items():
        body, headings = render_document(source, route, assets)
        key = route.with_suffix("").as_posix()
        def navigation_link(name, label):
            href = os.path.relpath(f"{name}.html", route.parent)
            current = ' aria-current="page"' if name == key else ""
            return f'<a href="{href}"{current}>{escape(label)}</a>'

        links = "".join(navigation_link(name, label) for name, label in NAV.items())
        sidebar = "".join(
            f'<div class="section"><p>{escape(title)}</p>'
            + "".join(navigation_link(name, PAGES[name]) for name in names)
            + "</div>"
            for title, names in SECTIONS.items()
        )
        page_kind = "overview" if key == "index" else "reference"
        stylesheet = os.path.relpath(STYLESHEET, route.parent)
        overview = os.path.relpath("index.html", route.parent)
        page = f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{escape(PAGES[key])} · Codegen Blueprint</title>
<link rel="stylesheet" href="{stylesheet}">
</head>
<body class="{page_kind}">
<a class="skip" href="#content">Skip to content</a>
<header>
<div class="header-inner"><a class="brand" href="{overview}">Codegen Blueprint</a><a href="{GITHUB}">GitHub</a></div>
<nav class="primary" aria-label="Primary">{links}</nav>
<details class="mobile-navigation">
<summary>Browse documentation</summary>
<nav aria-label="All documentation">{sidebar}</nav>
</details>
</header>
<div class="documentation">
<aside><nav aria-label="All documentation">{sidebar}</nav></aside>
<main id="content">{body}</main>
{page_outline(headings)}
</div>
<footer>Codegen Blueprint · <a href="{GITHUB}/blob/main/LICENSE">MIT</a></footer>
</body>
</html>
"""
        destination = site / route
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_text(page, encoding="utf-8")
    for asset in assets:
        destination = site / asset
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(ROOT / "docs" / asset, destination)
    shutil.copyfile(ROOT / "scripts/pages" / STYLESHEET, site / STYLESHEET)
    (site / ".nojekyll").touch()
    expected = set(ROUTES.values()) | assets | {Path(STYLESHEET), Path(".nojekyll")}
    actual = {path.relative_to(site) for path in site.rglob("*") if path.is_file()}
    if unexpected := actual - expected:
        raise ValueError(f"Use a fresh output directory. Unexpected files: {sorted(unexpected)}")
    check_site(site)
    print(f"Checked Markdown links and anchors; built {len(ROUTES)} Pages routes with {len(assets)} linked assets")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    build(args.output.resolve())
