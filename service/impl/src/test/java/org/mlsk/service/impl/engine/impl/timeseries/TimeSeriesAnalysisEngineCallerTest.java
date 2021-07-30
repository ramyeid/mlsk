package org.mlsk.service.impl.engine.impl.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeSeriesAnalysisEngineCallerTest {

  private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("host", "port");

  @Mock
  private EngineClientFactory engineClientFactory;
  @Mock
  private TimeSeriesAnalysisEngineClient engineClient;

  private TimeSeriesAnalysisEngineCaller timeSeriesAnalysisEngineCaller;

  @BeforeEach
  public void setUp() {
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION)).thenReturn(engineClient);
    timeSeriesAnalysisEngineCaller = new TimeSeriesAnalysisEngineCaller(SERVICE_INFORMATION, engineClientFactory);
  }

  @Test
  public void should_call_engine_client_forecast_on_forecast() {
    TimeSeriesAnalysisRequest request = buildRequest();

    timeSeriesAnalysisEngineCaller.forecast(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineClient).forecast(buildRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_engine_client_forecast_with_new_request_on_forecast_vs_actual() {
    TimeSeriesAnalysisRequest request = buildRequest();

    timeSeriesAnalysisEngineCaller.forecastVsActual(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineClient).forecast(buildExpectedRequestForForecastVsActual());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_engine_client_compute_forecast_accuary_on_compute_forecast_accuracy() {
    TimeSeriesAnalysisRequest request = buildRequest();

    timeSeriesAnalysisEngineCaller.computeForecastAccuracy(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineClient).computeForecastAccuracy(buildRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_call_engine_client_predict_on_predict() {
    TimeSeriesAnalysisRequest request = buildRequest();

    timeSeriesAnalysisEngineCaller.predict(request);

    InOrder inOrder = buildInOrder();
    inOrder.verify(engineClientFactory).buildTimeSeriesAnalysisEngineClient(SERVICE_INFORMATION);
    inOrder.verify(engineClient).predict(buildRequest());
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(engineClientFactory, engineClient);
  }

  private static TimeSeriesAnalysisRequest buildRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("date3", 3.);
    TimeSeriesRow row4 = new TimeSeriesRow("date4", 4.);
    TimeSeriesRow row5 = new TimeSeriesRow("date5", 5.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3, row4, row5);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "dateFormat");

    return new TimeSeriesAnalysisRequest(timeSeries, 3);
  }

  private static TimeSeriesAnalysisRequest buildExpectedRequestForForecastVsActual() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 2.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "dateFormat");

    return new TimeSeriesAnalysisRequest(timeSeries, 3);
  }
}
