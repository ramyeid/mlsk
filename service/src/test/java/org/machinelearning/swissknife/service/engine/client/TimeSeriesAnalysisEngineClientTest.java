package org.machinelearning.swissknife.service.engine.client;

import org.junit.jupiter.api.Test;
import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSeriesAnalysisEngineClientTest {

    private static final String ENGINE_HOST = "hp123";
    private static final String ENGINE_PORT = "6767";
    private static final ServiceInformation ENGINE_INFORMATION = new ServiceInformation(ENGINE_HOST, ENGINE_PORT);

    @Test
    public void should_delegate_forecast_call_to_engine() {
        RestClient restClient = mock(RestClient.class);
        TimeSeriesAnalysisEngineClient client = new TimeSeriesAnalysisEngineClient(restClient);
        TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
        when(restClient.post(any(), any(), any())).thenReturn(responseTimeSeries);

        List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
        TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
        TimeSeries actual_forecasted = client.forecast(timeSeriesAnalysisRequest);

        verify(restClient).post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        assertEquals(responseTimeSeries, actual_forecasted);
    }

    @Test
    public void should_delegate_compute_forecast_accuracy_call_to_engine() {
        RestClient restClient = mock(RestClient.class);
        TimeSeriesAnalysisEngineClient client = new TimeSeriesAnalysisEngineClient(restClient);
        when(restClient.post(any(), any(), any())).thenReturn("2");

        List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
        TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
        Double actual_accuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

        verify(restClient).post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, String.class);
        assertEquals(2.d, actual_accuracy);
    }

    @Test
    public void should_delegate_predict_call_to_engine() {
        RestClient restClient = mock(RestClient.class);
        TimeSeriesAnalysisEngineClient client = new TimeSeriesAnalysisEngineClient(restClient);
        TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
        when(restClient.post(any(), any(), any())).thenReturn(responseTimeSeries);

        List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
        TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
        TimeSeries actual_predicted = client.predict(timeSeriesAnalysisRequest);

        verify(restClient).post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        assertEquals(responseTimeSeries, actual_predicted);
    }

}