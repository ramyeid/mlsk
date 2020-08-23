package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.*;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.machinelearning.swissknife.ui.components.timeseries.TimeSeriesFrame;
import org.machinelearning.swissknife.ui.components.timeseries.TimeSeriesPlotPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class Application {

    public static ServiceInformation SERVICE_INFORMATION;

    public static void main(String[] args) throws ParseException {
        setServiceInformation(args);



        TimeSeriesFrame timeSeriesFrame = new TimeSeriesFrame();

        timeSeriesFrame.setVisible(true);

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
