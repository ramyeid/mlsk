package org.mlsk.ui.timeseries.component;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.ui.timeseries.request.TimeSeriesAnalysisRequestBuilder;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisServiceCaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.String.format;
import static org.mlsk.ui.component.builder.GridBagConstraintsBuilder.buildGridBagConstraints;
import static org.mlsk.ui.component.builder.JButtonBuilder.buildJButton;
import static org.mlsk.ui.component.builder.JLabelBuilder.buildJLabel;
import static org.mlsk.ui.component.builder.JTextFieldBuilder.buildJTextField;
import static org.mlsk.ui.component.button.CsvFileChooserButton.buildCsvFileChooserButton;
import static org.mlsk.ui.component.popup.ErrorPopup.tryPopup;
import static org.mlsk.ui.component.popup.ErrorPopup.tryPopupVoid;
import static org.mlsk.ui.timeseries.builder.DateFormatInformationButtonBuilder.buildDateFormatInformationButton;
import static org.mlsk.ui.timeseries.service.TimeSeriesAnalysisCommand.*;

public class TimeSeriesInputPanel extends JPanel implements ActionListener {

  private final JTextField csvAbsolutePathValue;
  private final JTextField dateColumnNameValue;
  private final JTextField valueColumnNameValue;
  private final JTextField dateFormatValue;
  private final JTextField numberOfValuesValue;
  private final JButton predictButton = buildJButton(PREDICT.getTitle(), this);
  private final JButton forecastButton = buildJButton(FORECAST.getTitle(), this);
  private final JButton forecastAndActualButton = buildJButton(FORECAST_VS_ACTUAL.getTitle(), this);
  private final JButton forecastAccuracyActualButton = buildJButton(FORECAST_ACCURACY.getTitle(), this);
  private final TimeSeriesAnalysisRequestBuilder requestBuilder;
  private final TimeSeriesAnalysisServiceCaller serviceCaller;

  public TimeSeriesInputPanel(TimeSeriesAnalysisServiceCaller serviceCaller) {
    this(serviceCaller, new TimeSeriesAnalysisRequestBuilder(), buildJTextField(20), buildJTextField(20),
        buildJTextField(20), buildJTextField(20), buildJTextField(4));
    this.setLayout(new GridBagLayout());

    this.add(buildConfigurationPanel(), buildGridBagConstraints(0, 0));
    this.add(buildButtonsPanel(), buildGridBagConstraints(0, 1));
  }

  @VisibleForTesting
  TimeSeriesInputPanel(TimeSeriesAnalysisServiceCaller serviceCaller,
                       TimeSeriesAnalysisRequestBuilder requestBuilder,
                       JTextField csvAbsolutePathValue, JTextField dateColumnNameValue,
                       JTextField valueColumnNameValue, JTextField dateFormatValue,
                       JTextField numberOfValuesValue) {
    this.serviceCaller = serviceCaller;
    this.requestBuilder = requestBuilder;
    this.csvAbsolutePathValue = csvAbsolutePathValue;
    this.dateColumnNameValue = dateColumnNameValue;
    this.valueColumnNameValue = valueColumnNameValue;
    this.dateFormatValue = dateFormatValue;
    this.numberOfValuesValue = numberOfValuesValue;
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    TimeSeriesAnalysisCommand command = fromString(actionEvent.getActionCommand());
    String dateColumnName = dateColumnNameValue.getText();
    String valueColumnName = valueColumnNameValue.getText();
    String dateFormat = dateFormatValue.getText();
    String csvLocation = csvAbsolutePathValue.getText();
    String numberOfValues = numberOfValuesValue.getText();

    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequest = tryPopup(() ->
            requestBuilder.buildRequest(dateColumnName, valueColumnName, dateFormat, csvLocation, numberOfValues),
        "Building Request");

    tryPopupVoid(
        () -> serviceCaller.callService(command, timeSeriesAnalysisRequest),
        format("Calling Service: %s", command));
  }

  private void onCsvFileChosen(String csvPath) {
    this.csvAbsolutePathValue.setText(csvPath);
    SwingUtilities.updateComponentTreeUI(this);
  }

  private JPanel buildButtonsPanel() {
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new GridBagLayout());

    buttonsPanel.add(predictButton, buildGridBagConstraints(0, 0));
    buttonsPanel.add(forecastButton, buildGridBagConstraints(1, 0));
    buttonsPanel.add(forecastAndActualButton, buildGridBagConstraints(2, 0));
    buttonsPanel.add(forecastAccuracyActualButton, buildGridBagConstraints(3, 0));
    return buttonsPanel;
  }

  private JPanel buildConfigurationPanel() {
    JPanel configurationPanel = new JPanel();
    configurationPanel.setLayout(new GridBagLayout());

    configurationPanel.add(buildJLabel("Csv Absolute Path"), buildGridBagConstraints(0, 0));
    configurationPanel.add(csvAbsolutePathValue, buildGridBagConstraints(1, 0));
    configurationPanel.add(buildCsvFileChooserButton(this::onCsvFileChosen), buildGridBagConstraints(2, 0));

    configurationPanel.add(buildJLabel("Date Column Name"), buildGridBagConstraints(0, 1));
    configurationPanel.add(dateColumnNameValue, buildGridBagConstraints(1, 1));

    configurationPanel.add(buildJLabel("Value Column Name"), buildGridBagConstraints(0, 2));
    configurationPanel.add(valueColumnNameValue, buildGridBagConstraints(1, 2));

    configurationPanel.add(buildJLabel("Date Format"), buildGridBagConstraints(0, 3));
    configurationPanel.add(dateFormatValue, buildGridBagConstraints(1, 3));
    configurationPanel.add(buildDateFormatInformationButton(), buildGridBagConstraints(2, 3));

    configurationPanel.add(buildJLabel("Number of Values"), buildGridBagConstraints(0, 4));
    configurationPanel.add(numberOfValuesValue, buildGridBagConstraints(1, 4));

    return configurationPanel;
  }
}
