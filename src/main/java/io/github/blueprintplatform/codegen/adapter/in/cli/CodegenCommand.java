package io.github.blueprintplatform.codegen.adapter.in.cli;

import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.SpringBootGenerateCommand;
import picocli.CommandLine.Command;

@Command(
    name = "codegen",
    mixinStandardHelpOptions = true,
    description = "Project scaffold generator based on explicit architectural blueprints",
    subcommands = {SpringBootGenerateCommand.class})
public class CodegenCommand {}
