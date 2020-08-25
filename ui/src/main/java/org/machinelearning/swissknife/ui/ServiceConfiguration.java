package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.*;
import org.machinelearning.swissknife.model.ServiceInformation;

public class ServiceConfiguration {

    private static ServiceConfiguration SERVICE_CONFIGURATION = null;

    private final ServiceInformation serviceInformation;

    private ServiceConfiguration(ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
    }

    public static ServiceInformation getServiceInformation() {
        return SERVICE_CONFIGURATION.serviceInformation;
    }

    static void buildServiceConfiguration(String... args) throws ParseException {
        Option enginePortsOption = new Option("port", "service-port", true, "Port of the service");
        enginePortsOption.setRequired(true);

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(enginePortsOption);
        CommandLine cmd;

        cmd = parser.parse(options, args);
        String port = cmd.getOptionValue("port");

        SERVICE_CONFIGURATION = new ServiceConfiguration(new ServiceInformation("localhost", port));
    }
}
