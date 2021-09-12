package org.mlsk.ui.timeseries.component;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisServiceCaller;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static org.mlsk.ui.setup.ServiceConfiguration.getServiceInformation;

public class TimeSeriesPanel extends JPanel {

  private final TimeSeriesInputPanel inputPanel;
  private final TimeSeriesOutputPanel outputPanel;

  public TimeSeriesPanel() {
    this(new TimeSeriesAnalysisServiceClient(getServiceInformation()));
  }

  @VisibleForTesting
  public TimeSeriesPanel(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
    this.inputPanel = new TimeSeriesInputPanel(buildServiceCaller(timeSeriesAnalysisServiceClient));
    this.outputPanel = new TimeSeriesOutputPanel();

    this.setLayout(new BorderLayout());

    this.add(inputPanel, NORTH);
    this.add(outputPanel, CENTER);
  }

  @VisibleForTesting
  TimeSeriesPanel(TimeSeriesInputPanel inputPanel, TimeSeriesOutputPanel outputPanel) {
    this.inputPanel = inputPanel;
    this.outputPanel = outputPanel;
  }

  @VisibleForTesting
  void onResult(TimeSeries initial, Object result, String title) {
    this.outputPanel.removeAll();

    if (result instanceof TimeSeries) {
      this.outputPanel.onTimeSeriesResult(initial, (TimeSeries) result, title);
    } else if (result instanceof Double) {
      this.outputPanel.onForecastAccuracyResult((Double) result);
    }

    SwingUtilities.updateComponentTreeUI(this);
  }

  private TimeSeriesAnalysisServiceCaller buildServiceCaller(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
    return new TimeSeriesAnalysisServiceCaller(timeSeriesAnalysisServiceClient, this::onResult);
  }
}