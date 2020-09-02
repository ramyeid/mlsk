package org.machinelearning.swissknife.service.engine.client.timeseries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;
import org.machinelearning.swissknife.service.engine.client.timeseries.exceptions.TimeSeriesAnalysisEngineRequestException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSeriesAnalysisEngineClientTest {

    @Mock
    private RestClient restClient;
    private TimeSeriesAnalysisEngineClient client;

    @BeforeEach
    public void setup() {
        this.client = new TimeSeriesAnalysisEngineClient(restClient);
    }

    @Test
    public void should_delegate_forecast_call_to_engine() {
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
    public void should_rethrow_time_series_exception_on_forecast_failure() {
        when(restClient.post(any(), any(), any())).thenThrow(new RuntimeException());

        try {
            client.forecast(mock(TimeSeriesAnalysisRequest.class));
            fail("should fail");
        } catch(TimeSeriesAnalysisEngineRequestException ignored){

        }
    }

    @Test
    public void should_delegate_compute_forecast_accuracy_call_to_engine() {
        when(restClient.post(any(), any(), any())).thenReturn(2.);

        List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
        TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
        Double actualAccuracy = client.computeForecastAccuracy(timeSeriesAnalysisRequest);

        verify(restClient).post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
        assertEquals(2.d, actualAccuracy);
    }

    @Test
    public void should_rethrow_time_series_exception_on_forecast_accuracy_failure() {
        when(restClient.post(any(), any(), any())).thenThrow(new RuntimeException());

        try {
            client.computeForecastAccuracy(mock(TimeSeriesAnalysisRequest.class));
            fail("should fail");
        } catch(TimeSeriesAnalysisEngineRequestException ignored){

        }
    }

    @Test
    public void should_delegate_predict_call_to_engine() {
        TimeSeries responseTimeSeries = new TimeSeries(singletonList(new TimeSeriesRow("1962", 3.)), "Date", "Value", "%Y");
        when(restClient.post(any(), any(), any())).thenReturn(responseTimeSeries);

        List<TimeSeriesRow> timeSeriesRows = asList(new TimeSeriesRow("1960", 1.), new TimeSeriesRow("1961", 2.));
        TimeSeries timeSeries = new TimeSeries(timeSeriesRows, "Date", "Value", "%Y");
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
        TimeSeries actual_predicted = client.predict(timeSeriesAnalysisRequest);

        verify(restClient).post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        assertEquals(responseTimeSeries, actual_predicted);
    }

    @Test
    public void should_rethrow_time_series_exception_on_predict_failure() {
        when(restClient.post(any(), any(), any())).thenThrow(new RuntimeException());

        try {
            client.predict(mock(TimeSeriesAnalysisRequest.class));
            fail("should fail");
        } catch(TimeSeriesAnalysisEngineRequestException ignored){

        }
    }
}