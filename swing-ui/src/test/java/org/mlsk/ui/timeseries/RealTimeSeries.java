package org.mlsk.ui.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.ui.timeseries.component.TimeSeriesPanel;
import org.mlsk.ui.timeseries.service.client.TimeSeriesAnalysisServiceClient;

import javax.swing.*;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RealTimeSeries {

  private static final TimeSeries EMPTY_TIME_SERIES = new TimeSeries(emptyList(), "", "", "");

  public static void main(String[] args) {
    JFrame mainFrame = new JFrame();
    mainFrame.add(new TimeSeriesPanel(mockTimeSeriesAnalysisServiceClient()));

    mainFrame.setSize(900, 600);
    mainFrame.setVisible(true);
  }

  private static TimeSeriesAnalysisServiceClient mockTimeSeriesAnalysisServiceClient() {
    TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient = mock(TimeSeriesAnalysisServiceClient.class);
    when(timeSeriesAnalysisServiceClient.forecast(any())).thenReturn(EMPTY_TIME_SERIES);
    when(timeSeriesAnalysisServiceClient.predict(any())).thenReturn(EMPTY_TIME_SERIES);

    return timeSeriesAnalysisServiceClient;
  }
}