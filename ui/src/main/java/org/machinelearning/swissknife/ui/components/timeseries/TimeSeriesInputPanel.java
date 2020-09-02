package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.lib.csv.CsvToTimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.components.utils.CsvFileChooserButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.StringJoiner;

import static org.machinelearning.swissknife.ui.components.timeseries.TimeSeriesActionListener.*;
import static org.machinelearning.swissknife.ui.components.utils.ComponentBuilder.newJButton;
import static org.machinelearning.swissknife.ui.components.utils.ErrorPopup.tryPopup;
import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesInputPanel extends JPanel {

    private final JTextField csvAbsolutePathValue;
    private final JTextField dateColumnNameValue;
    private final JTextField valueColumnNameValue;
    private final JTextField dateFormatValue;
    private final JTextField numberOfValuesValue;
    private final JButton predictButton;
    private final JButton forecastButton;
    private final JButton forecastAndActualButton;

    public TimeSeriesInputPanel() {
        this.csvAbsolutePathValue = new JTextField(20);
        this.dateColumnNameValue = new JTextField(20);
        this.valueColumnNameValue = new JTextField(20);
        this.dateFormatValue = new JTextField(20);
        this.numberOfValuesValue = new JTextField(4);
        this.predictButton = newJButton(PREDICT_COMMAND);
        this.forecastButton = newJButton(FORECAST_COMMAND);
        this.forecastAndActualButton = newJButton(FORECAST_VS_ACTUAL);

        this.setLayout(new GridBagLayout());

        this.add(buildConfigurationPanel(), buildGridBagConstraints(0, 0));
        this.add(buildButtonsPanel(), buildGridBagConstraints(0, 1));
    }

    private JPanel buildButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());

        buttonsPanel.add(predictButton, buildGridBagConstraints(0, 0));
        buttonsPanel.add(forecastButton, buildGridBagConstraints(1, 0));
        buttonsPanel.add(forecastAndActualButton, buildGridBagConstraints(2, 0));
        return buttonsPanel;
    }

    private JPanel buildConfigurationPanel() {
        JPanel configurationPanel = new JPanel();
        configurationPanel.setLayout(new GridBagLayout());

        configurationPanel.add(new JLabel("Csv Absolute Path"), buildGridBagConstraints(0, 0));
        configurationPanel.add(csvAbsolutePathValue, buildGridBagConstraints(1, 0));
        configurationPanel.add(new CsvFileChooserButton(this, csvAbsolutePathValue), buildGridBagConstraints(2, 0));

        configurationPanel.add(new JLabel("Date Column Name"), buildGridBagConstraints(0, 1));
        configurationPanel.add(dateColumnNameValue, buildGridBagConstraints(1, 1));

        configurationPanel.add(new JLabel("Value Column Name"), buildGridBagConstraints(0, 2));
        configurationPanel.add(valueColumnNameValue, buildGridBagConstraints(1, 2));

        configurationPanel.add(new JLabel("Date Format"), buildGridBagConstraints(0, 3));
        configurationPanel.add(dateFormatValue, buildGridBagConstraints(1, 3));
        configurationPanel.add(buildDateFormatInformationButton(), buildGridBagConstraints(2, 3));

        configurationPanel.add(new JLabel("Number of Values"), buildGridBagConstraints(0, 4));
        configurationPanel.add(numberOfValuesValue, buildGridBagConstraints(1, 4));

        return configurationPanel;
    }

    public void setActionListener(ActionListener actionListener) {
        predictButton.addActionListener(actionListener);
        forecastButton.addActionListener(actionListener);
        forecastAndActualButton.addActionListener(actionListener);
    }

    public TimeSeriesAnalysisRequest buildTimeSeriesRequest() {
        String dateColumnName = dateColumnNameValue.getText().trim();
        String valueColumnName = valueColumnNameValue.getText().trim();
        String dateFormat = dateFormatValue.getText().trim();
        String fileLocation = csvAbsolutePathValue.getText().trim();
        Integer numberOfValues = tryPopup(() -> Integer.parseInt(numberOfValuesValue.getText()), "Could not format 'number of values' to Integer");

        TimeSeries timeSeries = tryPopup(() -> CsvToTimeSeries.toTimeSeries(fileLocation, dateColumnName, valueColumnName, dateFormat),
                            "Could not parse CSV File");
        return new TimeSeriesAnalysisRequest(timeSeries, numberOfValues);
    }

    private JButton buildDateFormatInformationButton() {

        return newJButton("?", e -> {
            StringJoiner stringBuilder = new StringJoiner("\n");
            stringBuilder.add("Date format according to Java SimpleDate:");
            stringBuilder.add("year: yy or yyyy");
            stringBuilder.add("month: MM");
            stringBuilder.add("day: dd");
            stringBuilder.add("hour (0-12): hh");
            stringBuilder.add("hour (0-23): HH");
            stringBuilder.add("minute: mm");
            stringBuilder.add("seconds: ss");
            stringBuilder.add("milliseconds: SSS");
            JOptionPane.showMessageDialog(null, stringBuilder.toString(), "Date Format information", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
