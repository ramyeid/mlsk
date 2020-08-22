package org.machinelearning.swissknife.service;

import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.service.engine.deployment.EngineCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import org.apache.commons.cli.*;

@SpringBootApplication
public class Application {

    private static final List<String> enginePorts = new ArrayList<>();

    @Bean
    public Orchestrator buildOrchestrator() {
        EngineCreator engineCreator = new EngineCreator();
        List<Engine> engines = enginePorts.stream()
                                 .map(port -> engineCreator.createEngine(new ServiceInformation("localhost", port)))
                                 .collect(toList());
        return new Orchestrator(engines);
    }

    public static void main(String... args) throws ParseException {
        List<String> enginePortsFromArguments = getEnginePortsFromArguments(args);
        enginePorts.addAll(enginePortsFromArguments);
        SpringApplication.run(Application.class, args);
    }

    private static List<String> getEnginePortsFromArguments(String[] args) throws ParseException {
        Option enginePortsOption = new Option("ports", "engine-ports", true, "Ports of the engine to be launched");
        enginePortsOption.setRequired(true);

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(enginePortsOption);
        CommandLine cmd;

        cmd = parser.parse(options, args);
        String enginePortsAsString = cmd.getOptionValue("ports");
        return Arrays.asList(enginePortsAsString.split(","));
    }
}
