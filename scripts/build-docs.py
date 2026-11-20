#!/usr/bin/env python3
"""Build Pages from the same Markdown files used by GitHub and check local links."""

import argparse
from html import escape
from html.parser import HTMLParser
import os
from pathlib import Path
import shutil
from urllib.parse import quote, unquote, urlsplit

import markdown
from markdown.extensions import Extension
from markdown.treeprocessors import Treeprocessor

ROOT = Path(__file__).resolve().parents[1]
STYLESHEET = "site.css"
GITHUB = "https://github.com/blueprint-platform/codegen-blueprint"
NAV = {
    "index": "Overview",
    "quickstart": "Quickstart",
    "demo/executable-architecture-proof": "Proof",
    "compatibility": "Compatibility",
    "cli": "CLI",
    "architecture/executable-architecture-contract": "Architecture",
}
PAGES = {
    **NAV,
    "architecture/architecture-guardrails-rulebook": "Guardrails",
    "guides/migrate-to-2.0": "Migration",
    "releases/2.0.0": "Release notes",
    "policies/release-discipline": "Release readiness",
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
        if not target.is_relative_to(ROOT) or not target.is_file():
            raise ValueError(f"{self.source.relative_to(ROOT)}: missing local target {raw}")
        if self.route is None:
            return None

        fragment = f"#{url.fragment}" if url.fragment else ""
        if target in ROUTES:
            destination = ROUTES[target]
        elif tag == "img" or target.suffix in {".txt", ".svg", ".png"}:
            destination = target.relative_to(ROOT / "docs")
            self.assets.add(destination)
        else:
            return f"{GITHUB}/blob/main/{quote(str(target.relative_to(ROOT)))}{fragment}"
        return quote(os.path.relpath(destination, self.route.parent)) + fragment


class LocalLinks(Extension):
    def __init__(self, source, route, assets):
        self.processor = Links(source, route, assets)

    def extendMarkdown(self, renderer):
        renderer.treeprocessors.register(self.processor, "local_links", 1)


def render(source, route, assets):
    return markdown.markdown(
        source.read_text(encoding="utf-8"),
        extensions=["fenced_code", "tables", "toc", LocalLinks(source, route, assets)],
        output_format="html",
    )


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


def build(site):
    assets = set()
    for source in ("README.md", "AGENTS.md", "CONTRIBUTING.md", "CHANGELOG.md", "SECURITY.md"):
        render(ROOT / source, None, assets)
    for source, route in ROUTES.items():
        body = render(source, route, assets)
        key = route.with_suffix("").as_posix()
        links = []
        for name, label in NAV.items():
            href = os.path.relpath(f"{name}.html", route.parent)
            current = ' aria-current="page"' if name == key else ""
            links.append(f'<a href="{href}"{current}>{escape(label)}</a>')
        stylesheet = os.path.relpath(STYLESHEET, route.parent)
        page = f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{escape(PAGES[key])} · Codegen Blueprint</title>
<link rel="stylesheet" href="{stylesheet}">
</head>
<body>
<a class="skip" href="#content">Skip to content</a>
<header>
<div class="header-inner"><strong>Codegen Blueprint</strong><a href="{GITHUB}">GitHub</a></div>
<nav aria-label="Documentation">{"".join(links)}</nav>
</header>
<main id="content">{body}</main>
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
    shutil.copyfile(ROOT / "docs/site" / STYLESHEET, site / STYLESHEET)
    (site / ".nojekyll").touch()
    expected = set(ROUTES.values()) | assets | {Path(STYLESHEET), Path(".nojekyll")}
    actual = {path.relative_to(site) for path in site.rglob("*") if path.is_file()}
    if unexpected := actual - expected:
        raise ValueError(f"Use a fresh output directory. Unexpected files: {sorted(unexpected)}")
    check_site(site)
    print(f"Built and checked {len(ROUTES)} Pages routes and {len(assets)} linked assets")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    build(args.output.resolve())
