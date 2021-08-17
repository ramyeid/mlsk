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

import static com.google.common.collect.Lists.newArrayList;
import static org.mlsk.ui.components.timeseries.TimeSeriesActionListener.*;
import static org.mlsk.ui.components.utils.ComponentBuilder.newJButton;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesActionListenerTest {

  @Mock
  private TimeSeriesInputPanel timeSeriesInputPanel;
  @Mock
  private TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;
  @Mock
  private TriFunction<TimeSeries, Object, String> onResults;

  private static final TimeSeriesAnalysisRequest TIME_SERIES_ANALYSIS_REQUEST = buildTimeSeriesRequest();
  private static final TimeSeries TIME_SERIES_RESULT = buildTimeSeriesResult();
  private static final Double FORECAST_ACCURACY_RESULT = buildForecastAccuracyResult();

  @BeforeEach
  public void setUp() {
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

    timeSeriesActionListener.actionPerformed(new ActionEvent(newJButton(FORECAST_VS_ACTUAL_COMMAND), 0, FORECAST_VS_ACTUAL_COMMAND));

    verify(timeSeriesAnalysisServiceClient).forecastVsActual(TIME_SERIES_ANALYSIS_REQUEST);
    verify(onResults).apply(TIME_SERIES_ANALYSIS_REQUEST.getTimeSeries(), TIME_SERIES_RESULT, FORECAST_COMMAND.toLowerCase());
  }

  @Test
  public void should_delegate_call_to_forecast_accuracy() {
    when(timeSeriesAnalysisServiceClient.computeForecastAccuracy(any())).thenReturn(FORECAST_ACCURACY_RESULT);
    TimeSeriesActionListener timeSeriesActionListener = new TimeSeriesActionListener(timeSeriesInputPanel, timeSeriesAnalysisServiceClient, onResults);

    timeSeriesActionListener.actionPerformed(new ActionEvent(newJButton(FORECAST_ACCURACY_COMMAND), 0, FORECAST_ACCURACY_COMMAND));

    verify(timeSeriesAnalysisServiceClient).computeForecastAccuracy(TIME_SERIES_ANALYSIS_REQUEST);
    verify(onResults).apply(TIME_SERIES_ANALYSIS_REQUEST.getTimeSeries(), FORECAST_ACCURACY_RESULT, FORECAST_ACCURACY_COMMAND.toLowerCase());
  }

  private static TimeSeriesAnalysisRequest buildTimeSeriesRequest() {
    TimeSeries timeSeries = new TimeSeries(newArrayList(new TimeSeriesRow("date", 1.)), "Date", "Value", "yy");
    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  private static TimeSeries buildTimeSeriesResult() {
    return new TimeSeries(newArrayList(new TimeSeriesRow("date2", 2.)), "Date", "Value", "yy");
  }

  private static Double buildForecastAccuracyResult() {
    return 95.34;
  }
}