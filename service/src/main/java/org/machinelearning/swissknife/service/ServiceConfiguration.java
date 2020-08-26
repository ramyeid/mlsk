package org.machinelearning.swissknife.service;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceConfiguration {

    private static ServiceConfiguration SERVICE_CONFIGURATION = null;

    private final String logsPath;
    private final String enginePath;
    private final List<String> enginePorts;

    private ServiceConfiguration(String logsPath, String enginePath, List<String> enginePorts) {
        this.logsPath = logsPath;
        this.enginePath = enginePath;
        this.enginePorts = enginePorts;
    }

    public static String getLogsPath() {
        return SERVICE_CONFIGURATION.logsPath;
    }

    public static String getEnginePath() {
        return SERVICE_CONFIGURATION.enginePath;
    }

    public static List<String> getEnginePorts() {
        return SERVICE_CONFIGURATION.enginePorts;
    }

    static void buildServiceConfiguration(String... args) throws ParseException {
        Option enginePortsOption = new Option("enginePorts", "engine-ports", true, "Ports of the engine to be launched");
        Option logsPathOption = new Option("logsPath", "logs-path", true, "absolute path towards Logs");
        Option enginePathOption = new Option("enginePath", "engine-path", true, "absolute path towards engine.py");
        CommandLineParser parser = new DefaultParser();

        enginePortsOption.setRequired(true);
        logsPathOption.setRequired(true);
        enginePathOption.setRequired(true);

        Options options = new Options();
        options.addOption(enginePortsOption);
        options.addOption(logsPathOption);
        options.addOption(enginePathOption);

        CommandLine cmd = parser.parse(options, args);

        String logsPath = cmd.getOptionValue("logsPath");
        String enginePath = cmd.getOptionValue("enginePath");
        List<String> enginePorts = Arrays.stream(cmd.getOptionValue("enginePorts")
                .split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        SERVICE_CONFIGURATION = new ServiceConfiguration(logsPath, enginePath, enginePorts);
    }

    @VisibleForTesting
    public static void buildForTest(String logsPath, String enginePath, List<String> enginePorts) {
        SERVICE_CONFIGURATION = new ServiceConfiguration(logsPath, enginePath, enginePorts);
    }
}
