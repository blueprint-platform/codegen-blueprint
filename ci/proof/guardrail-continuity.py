#!/usr/bin/env python3
"""Prove the Basic/Strict dependency and context-cycle guarantees in delivered projects."""

import argparse
import os
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET

PACKAGE = "com.example.greeting"
STANDARD = {
    "service/Upward.java": "class Upward { com.example.greeting.controller.Presentation next; }",
    "controller/Presentation.java": "public class Presentation {}",
    "config/CycleLeft.java": "public class CycleLeft { com.example.greeting.service.CycleRight next; }",
    "service/CycleRight.java": "public class CycleRight { com.example.greeting.config.CycleLeft next; }",
}
HEXAGONAL = {
    "application/usecase/Inward.java": "class Inward { com.example.greeting.adapter.out.Concrete next; }",
    "adapter/out/Concrete.java": "public class Concrete { com.example.greeting.application.RootType next; }",
    "application/RootType.java": "public class RootType {}",
    "domain/model/Outward.java": "class Outward { com.example.greeting.adapter.out.Concrete next; }",
}
CYCLES = "each_bounded_context_root_must_be_free_of_cycles"
COMMON_HEX = "application_must_not_depend_on_adapters"
EXPECTED = {
    ("standard", "basic"): {
        ("StandardBasicArchitectureRulesTest", "services_must_not_depend_on_controllers"): "Upward",
        ("StandardBasicArchitectureRulesTest", CYCLES): "CycleLeft",
    },
    ("standard", "strict"): {
        ("StandardStrictLayerDependencyRulesTest", "services_must_not_depend_on_controllers"): "Upward",
        ("StandardStrictPackageCyclesTest", CYCLES): "CycleLeft",
    },
    ("hexagonal", "basic"): {
        ("HexagonalBasicArchitectureRulesTest", COMMON_HEX): "Inward",
        ("HexagonalBasicArchitectureRulesTest", "domain_must_not_depend_on_outer_families"): "Outward",
        ("HexagonalBasicArchitectureRulesTest", CYCLES): "Concrete",
    },
    ("hexagonal", "strict"): {
        ("HexagonalStrictPortsIsolationTest", COMMON_HEX): "Inward",
        ("HexagonalStrictPortsIsolationTest", "adapters_must_not_depend_on_application_implementation"): "RootType",
        ("HexagonalStrictDomainPurityTest", "domain_must_depend_only_on_jdk_and_domain"): "Outward",
        ("HexagonalStrictPackageCyclesTest", CYCLES): "Concrete",
    },
}


def verify(project, log, expected=None):
    with log.open("w") as output:
        result = subprocess.run(
            ["sh", "./mvnw", "-B", "-ntp", "clean", "verify"],
            cwd=project, stdout=output, stderr=subprocess.STDOUT,
        )
    reports = project / "target/surefire-reports"
    suites = list(reports.glob("TEST-*.architecture.archunit.*Test.xml"))
    if expected is None:
        http = list((project / "target/failsafe-reports").glob("TEST-*GreetingSampleHttpIT.xml"))
        if result.returncode or not suites or not any(
            int(ET.parse(report).getroot().get("tests", "0")) > 0 for report in http
        ):
            raise SystemExit(f"Expected working guardrails and HTTP sample: {log}")
        print(f"PASS {log.stem}", flush=True)
        return

    if result.returncode == 0:
        raise SystemExit(f"Unexpected passing build: {log}")
    failures = {}
    for report in suites:
        for case in ET.parse(report).iter("testcase"):
            failure = case.find("failure")
            if failure is not None:
                key = (case.get("classname", "").rsplit(".", 1)[-1], case.get("name"))
                failures[key] = failure.get("message", "") + (failure.text or "")
    for rule, edge in expected.items():
        if edge not in failures.get(rule, ""):
            raise SystemExit(f"Expected {'#'.join(rule)} to report {edge}: {log}")
        print(f"EXPECTED_FAIL {log.stem} {'#'.join(rule)} ({edge})", flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("jar", type=Path)
    parser.add_argument("output", type=Path, help="Fresh directory for generated projects and logs")
    parser.add_argument("--boot", choices=("4.1", "4.2"), default="4.2")
    parser.add_argument("--java", choices=("21", "25"), default="25")
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=False)
    for (layout, mode), expected in EXPECTED.items():
        slug = f"{layout}-{mode}"
        project = args.output / slug
        with (args.output / f"{slug}-generation.log").open("w") as log:
            subprocess.run([
                os.environ.get("GENERATOR_JAVA", "java"),
                "-Dhttps.proxyHost=127.0.0.1", "-Dhttps.proxyPort=1",
                "-jar", str(args.jar.resolve()), "--cli", "springboot",
                "--group-id", "com.example", "--artifact-id", slug, "--name", "Greeting",
                "--package-name", PACKAGE, "--description", "Guardrail continuity fixture",
                "--layout", layout, "--guardrails", mode, "--sample-code", "basic",
                "--boot", args.boot, "--java", args.java, "--target-dir", str(args.output.resolve()),
            ], check=True, stdout=log, stderr=subprocess.STDOUT)
        verify(project, args.output / f"{slug}-green.log")
        base = project / "src/main/java" / PACKAGE.replace(".", "/")
        sources = STANDARD if layout == "standard" else HEXAGONAL
        for relative, declaration in sources.items():
            file = base / relative
            family = ".".join(Path(relative).parent.parts)
            with file.open("x", encoding="utf-8") as source:
                source.write(f"package {PACKAGE}.{family};\n{declaration}\n")
        verify(project, args.output / f"{slug}-red.log", expected)
        for relative in sources:
            (base / relative).unlink()
        verify(project, args.output / f"{slug}-restored.log")


if __name__ == "__main__":
    main()
