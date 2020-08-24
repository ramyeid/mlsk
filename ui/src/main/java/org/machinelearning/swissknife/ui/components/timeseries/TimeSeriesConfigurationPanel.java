package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.components.utils.CsvFileChooserButton;

import javax.swing.*;
import java.awt.*;

import static org.machinelearning.swissknife.ui.components.utils.ComponentBuilder.newJButton;
import static org.machinelearning.swissknife.ui.components.utils.ErrorPopup.tryPopup;
import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesConfigurationPanel extends JPanel {

    private final JTextField csvAbsolutePathValue;
    private final JTextField dateColumnNameValue;
    private final JTextField valueColumnNameValue;
    private final JTextField dateFormatValue;
    private final JTextField numberOfValuesValue;

    public TimeSeriesConfigurationPanel() {
        this.dateColumnNameValue = new JTextField(20);
        this.valueColumnNameValue = new JTextField(20);
        this.dateFormatValue = new JTextField(20);
        this.numberOfValuesValue = new JTextField(4);
        this.csvAbsolutePathValue = new JTextField(20);

        this.setLayout(new GridBagLayout());

        this.add(new JLabel("Csv Absolute Path"), buildGridBagConstraints(0, 0));
        this.add(csvAbsolutePathValue, buildGridBagConstraints(1, 0));
        this.add(new CsvFileChooserButton(this, csvAbsolutePathValue), buildGridBagConstraints(2, 0));

        this.add(new JLabel("Date Column Name"), buildGridBagConstraints(0, 1));
        this.add(dateColumnNameValue, buildGridBagConstraints(1, 1));

        this.add(new JLabel("Value Column Name"), buildGridBagConstraints(0, 2));
        this.add(valueColumnNameValue, buildGridBagConstraints(1, 2));

        this.add(new JLabel("Date Format"), buildGridBagConstraints(0, 3));
        this.add(dateFormatValue, buildGridBagConstraints(1, 3));
        this.add(buildDateFormatInformationButton(), buildGridBagConstraints(2, 3));

        this.add(new JLabel("Number of Values"), buildGridBagConstraints(0, 4));
        this.add(numberOfValuesValue, buildGridBagConstraints(1, 4));
    }

    public TimeSeriesAnalysisRequest buildTimeSeriesRequest() {
        String dateColumnName = dateColumnNameValue.getText().trim();
        String valueColumnName = valueColumnNameValue.getText().trim();
        String dateFormat = dateFormatValue.getText().trim();
        String fileLocation = csvAbsolutePathValue.getText().trim();
        Integer numberOfValues = tryPopup(() -> Integer.parseInt(numberOfValuesValue.getText()), "Could not format number of value to integer");

        TimeSeries timeSeries = tryPopup(() -> TimeSeries.buildFromCsv(fileLocation, dateColumnName, valueColumnName, dateFormat), "Could not parse CSV File");
        return new TimeSeriesAnalysisRequest(timeSeries, numberOfValues);
    }

    private JButton buildDateFormatInformationButton() {

        return newJButton("?", e -> {
            String information = "Date format according to Java SimpleDate: \n" +
                    "y   = year   (yy or yyyy)\n" +
                    "M   = month  (MM)\n" +
                    "d   = day in month (dd)\n" +
                    "h   = hour (0-12)  (hh)\n" +
                    "H   = hour (0-23)  (HH)\n" +
                    "m   = minute in hour (mm)\n" +
                    "s   = seconds (ss)\n" +
                    "S   = milliseconds (SSS)\n";
            JOptionPane.showMessageDialog(null, information, "Date Format information", JOptionPane.INFORMATION_MESSAGE);
        });
    }

}
