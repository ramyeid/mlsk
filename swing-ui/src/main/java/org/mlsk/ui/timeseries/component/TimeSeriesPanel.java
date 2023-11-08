package org.mlsk.ui.timeseries.component;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.ui.timeseries.service.TimeSeriesAnalysisServiceCaller;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static org.mlsk.ui.setup.ServiceConfiguration.getEndpoint;

public class TimeSeriesPanel extends JPanel {

  private final TimeSeriesInputPanel inputPanel;
  private final TimeSeriesOutputPanel outputPanel;

  public TimeSeriesPanel() {
    this(new TimeSeriesAnalysisServiceClient(getEndpoint()));
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
  void onResult(TimeSeriesModel initial, Object result, String title) {
    this.outputPanel.removeAll();

    if (result instanceof TimeSeriesModel) {
      this.outputPanel.onTimeSeriesResult(initial, (TimeSeriesModel) result, title);
    } else if (result instanceof BigDecimal) {
      this.outputPanel.onForecastAccuracyResult(((BigDecimal) result).doubleValue());
    }

    SwingUtilities.updateComponentTreeUI(this);
  }

  private TimeSeriesAnalysisServiceCaller buildServiceCaller(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
    return new TimeSeriesAnalysisServiceCaller(timeSeriesAnalysisServiceClient, this::onResult);
  }
}