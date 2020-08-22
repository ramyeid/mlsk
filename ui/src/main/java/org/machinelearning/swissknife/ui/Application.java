package org.machinelearning.swissknife.ui;

import org.apache.commons.cli.*;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.timeseries.TimeSeriesAnalysisRestClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Application {

    public static void main(String[] args) throws ParseException {
        String servicePort = getServicePort(args);

        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("Press");
        frame.getContentPane().add(button, 0);

        JLabel jLabel = new JLabel();
        jLabel.setText("Asd");
        frame.getContentPane().add(jLabel, 1);

        button.addActionListener(e -> {
            String locationOfCsv = "/Users/ramyeid/Documents/machine-learning-swissknife/resources/data_example/AirPassengers.csv";
            String dateColumnName = "Date";
            String valueColumnName = "Passengers";
            String dateFormat = "%Y-%m";
            int numberOfValues = 5;
            try {
                TimeSeries timeSeries = TimeSeries.buildFromCsv(locationOfCsv, dateColumnName, valueColumnName, dateFormat);

                System.out.println(timeSeries.getRows().size());
                System.out.println(servicePort);

                ServiceInformation serviceInformation = new ServiceInformation("localhost", servicePort);
                TimeSeriesAnalysisRestClient timeSeriesAnalysisRestClient = new TimeSeriesAnalysisRestClient(serviceInformation);

                TimeSeries predictedTimeSeries = timeSeriesAnalysisRestClient.predict(new TimeSeriesAnalysisRequest(timeSeries, numberOfValues));

                System.out.println(timeSeries.equals(predictedTimeSeries));

                TimeSeries forecastTimeSeries = timeSeriesAnalysisRestClient.forecast(new TimeSeriesAnalysisRequest(timeSeries, numberOfValues));

                System.out.println(timeSeries.equals(forecastTimeSeries));

                Double accuracy = timeSeriesAnalysisRestClient.computeForecastAccuracy(new TimeSeriesAnalysisRequest(timeSeries, 2));

                System.out.println(accuracy);

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        frame.pack();
        frame.setVisible(true);

    }

    private static String getServicePort(String[] args) throws ParseException {
        Option enginePortsOption = new Option("port", "service-port", true, "Port of the service");
        enginePortsOption.setRequired(true);

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(enginePortsOption);
        CommandLine cmd;

        cmd = parser.parse(options, args);
        return cmd.getOptionValue("port");
    }

}
