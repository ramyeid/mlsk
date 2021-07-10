package org.mlsk.ui.components.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.components.utils.TriFunction;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.ActionEvent;

import static java.util.Collections.singletonList;
import static org.mlsk.ui.components.timeseries.TimeSeriesActionListener.*;
import static org.mlsk.ui.components.utils.ComponentBuilder.newJButton;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSeriesActionListenerTest {

  @Mock
  private TimeSeriesInputPanel timeSeriesInputPanel;
  @Mock
  private TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;
  @Mock
  private TriFunction<TimeSeries, TimeSeries, String> onResults;
  private static final TimeSeriesAnalysisRequest TIME_SERIES_ANALYSIS_REQUEST = buildTimeSeriesRequest();
  private static final TimeSeries TIME_SERIES_RESULT = buildTimeSeriesResult();

  @BeforeEach
  public void setup() {
    when(timeSeriesInputPanel.buildTimeSeriesRequest()).thenReturn(TIME_SERIES_ANALYSIS_REQUEST);
  }

  @Test
  public void should_delegate_call_to_predict() {
    when(timeSeriesAnalysisServiceClient.predict(any())).thenReturn(TIME_SERIES_RESULT);
    TimeSeriesActionListener timeSeriesActionListener = new TimeSeriesActionListener(timeSeriesInputPanel, timeSeriesAnalysisServiceClient, onResults);

    timeSeriesActionListener.actionPerformed(new ActionEvent(newJButton(PREDICT_COMMAND), 0, PREDICT_COMMAND));

    verify(timeSeriesAnalysisServiceClient).predict(TIME_SERIES_ANALYSIS_REQUEST);
    verify(onResults).apply(TIME_SERIES_ANALYSIS_REQUEST.getTimeSeries(), TIME_SERIES_RESULT, PREDICT_COMMAND.toLowerCase());
  }

  @Test
  public void should_delegate_call_to_forecast() {
    when(timeSeriesAnalysisServiceClient.forecast(any())).thenReturn(TIME_SERIES_RESULT);
    TimeSeriesActionListener timeSeriesActionListener = new TimeSeriesActionListener(timeSeriesInputPanel, timeSeriesAnalysisServiceClient, onResults);

    timeSeriesActionListener.actionPerformed(new ActionEvent(newJButton(FORECAST_COMMAND), 0, FORECAST_COMMAND));

    verify(timeSeriesAnalysisServiceClient).forecast(TIME_SERIES_ANALYSIS_REQUEST);
    verify(onResults).apply(TIME_SERIES_ANALYSIS_REQUEST.getTimeSeries(), TIME_SERIES_RESULT, FORECAST_COMMAND.toLowerCase());
  }

  @Test
  public void should_delegate_call_to_forecast_vs_actual() {
    when(timeSeriesAnalysisServiceClient.forecastVsActual(any())).thenReturn(TIME_SERIES_RESULT);
    TimeSeriesActionListener timeSeriesActionListener = new TimeSeriesActionListener(timeSeriesInputPanel, timeSeriesAnalysisServiceClient, onResults);

    timeSeriesActionListener.actionPerformed(new ActionEvent(newJButton(FORECAST_VS_ACTUAL), 0, FORECAST_VS_ACTUAL));

    verify(timeSeriesAnalysisServiceClient).forecastVsActual(TIME_SERIES_ANALYSIS_REQUEST);
    verify(onResults).apply(TIME_SERIES_ANALYSIS_REQUEST.getTimeSeries(), TIME_SERIES_RESULT, FORECAST_COMMAND.toLowerCase());
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesRequest() {
    TimeSeries timeSeries = new TimeSeries(singletonList(new TimeSeriesRow("date", 1.)), "Date", "Value", "yy");
    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static TimeSeries buildTimeSeriesResult() {
    return new TimeSeries(singletonList(new TimeSeriesRow("date2", 2.)), "Date", "Value", "yy");
  }
}