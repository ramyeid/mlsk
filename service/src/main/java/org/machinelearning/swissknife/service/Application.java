package org.machinelearning.swissknife.service;

import org.apache.commons.cli.*;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.service.engine.deployment.EngineCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
public class Application {

    private static final List<String> enginePorts = new ArrayList<>();
    public static String LOGS_PATH = "";
    public static String ENGINE_PATH = "";

    @Bean
    public Orchestrator buildOrchestrator() {
        EngineCreator engineCreator = new EngineCreator();
        List<Engine> engines = enginePorts.stream()
                                 .map(engineCreator::createEngine)
                                 .collect(toList());
        return new Orchestrator(engines);
    }

    public static void main(String... args) throws ParseException {
        Option enginePortsOption = new Option("ports", "engine-ports", true, "Ports of the engine to be launched");
        enginePortsOption.setRequired(true);

        Option logsPath = new Option("logsPath", "logsPath", true, "absolute path towards Logs");
        logsPath.setRequired(true);

        Option enginePath = new Option("enginePath", "enginePath", true, "absolute path towards engine.py");
        enginePath.setRequired(true);

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(enginePortsOption);
        options.addOption(logsPath);
        options.addOption(enginePath);

        CommandLine cmd = parser.parse(options, args);

        LOGS_PATH = cmd.getOptionValue("logsPath");
        ENGINE_PATH = cmd.getOptionValue("enginePath");
        enginePorts.addAll(Arrays.asList(cmd.getOptionValue("ports").split(",")));

        SpringApplication.run(Application.class, args);
    }
}
