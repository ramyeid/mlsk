package org.mlsk.ui.components.timeseries;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.ui.client.timeseries.TimeSeriesAnalysisServiceClient;

import javax.swing.*;
import java.awt.*;

import static org.mlsk.ui.ServiceConfiguration.getServiceInformation;

public class TimeSeriesPanel extends JPanel {

  private final TimeSeriesInputPanel inputPanel;
  private final TimeSeriesOutputPanel outputPanel;

  public TimeSeriesPanel() {
    this(new TimeSeriesAnalysisServiceClient(getServiceInformation()));
  }

  @VisibleForTesting
  public TimeSeriesPanel(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
    this.setLayout(new BorderLayout());
    this.outputPanel = new TimeSeriesOutputPanel();
    this.inputPanel = new TimeSeriesInputPanel();
    this.inputPanel.setActionListener(new TimeSeriesActionListener(inputPanel, timeSeriesAnalysisServiceClient, this::onResult));

    this.add(inputPanel, BorderLayout.NORTH);
    this.add(outputPanel, BorderLayout.CENTER);
  }

  private void onResult(TimeSeries initial, Object result, String title) {
    this.outputPanel.removeAll();
    if (result instanceof TimeSeries) {
      this.outputPanel.onTimeSeriesResult(initial, (TimeSeries) result, title);
    } else if (result instanceof Double) {
      this.outputPanel.onForecastAccuracyResult((Double) result);
    }
    SwingUtilities.updateComponentTreeUI(this);
  }
}