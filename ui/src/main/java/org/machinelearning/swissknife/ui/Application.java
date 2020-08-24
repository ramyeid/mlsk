package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.*;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.ui.components.startup.MainFrame;

public class Application {

    public static ServiceInformation SERVICE_INFORMATION;

    public static void main(String[] args) throws ParseException {
        setServiceInformation(args);
        new MainFrame();
    }

    private static void setServiceInformation(String[] args) throws ParseException {
        Option enginePortsOption = new Option("port", "service-port", true, "Port of the service");
        enginePortsOption.setRequired(true);

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(enginePortsOption);
        CommandLine cmd;

        cmd = parser.parse(options, args);
        String port = cmd.getOptionValue("port");

        SERVICE_INFORMATION =  new ServiceInformation("localhost", port);
    }

}
