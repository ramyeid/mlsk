package org.machinelearning.swissknife.service.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;
import org.machinelearning.swissknife.service.engine.timeseries.TimeSeriesAnalysisEngineClient;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.machinelearning.swissknife.model.EngineState.COMPUTING;
import static org.machinelearning.swissknife.model.EngineState.WAITING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineImplTest {

    @Mock
    private EngineClientFactory engineClientFactory;
    @Mock
    private TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient;
    private final ServiceInformation serviceInformation = new ServiceInformation("host", "port");
    private final TimeSeriesRow timeSeriesRow = new TimeSeriesRow("1960", 1.);
    private final TimeSeriesRow timeSeriesRow1 = new TimeSeriesRow("1961", 2.);
    private final TimeSeriesRow timeSeriesRow2 = new TimeSeriesRow("1962", 3.);
    private final TimeSeries timeSeries = new TimeSeries(asList(timeSeriesRow, timeSeriesRow1), "Date", "Value", "%Y");
    private final TimeSeries timeSeries2 = new TimeSeries(singletonList(timeSeriesRow2), "Date", "Value", "%Y");
    private final TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(timeSeries, 1);
    private final Double accuracy = 33.;

    @BeforeEach
    public void setUp() {
        when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(any())).thenReturn(timeSeriesAnalysisEngineClient);
    }

    @Test
    public void should_append_forecasted_values_to_initial_time_series_on_forecast() {
        when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING));

        TimeSeries actualTimeSeries = engineImpl.forecast(timeSeriesAnalysisRequest);

        TimeSeries expectedTimeSeries = new TimeSeries(asList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2), "Date", "Value", "%Y");
        verify(timeSeriesAnalysisEngineClient).forecast(any());
        assertEquals(expectedTimeSeries, actualTimeSeries);
        assertEquals(WAITING, engineImpl.getState());
    }

    @Test
    public void should_modify_state_to_computing_and_then_waiting_on_forecast() {
        when(timeSeriesAnalysisEngineClient.forecast(any())).thenReturn(timeSeries2);
        AtomicReference<EngineState> mockEngineState = mock(AtomicReference.class);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, mockEngineState);

        engineImpl.forecast(timeSeriesAnalysisRequest);

        InOrder inOrder = inOrder(mockEngineState);
        inOrder.verify(mockEngineState).set(COMPUTING);
        inOrder.verify(mockEngineState).set(WAITING);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_delegate_compute_forecast_accuracy_to_engine() {
        when(timeSeriesAnalysisEngineClient.computeForecastAccuracy(any())).thenReturn(accuracy);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING));

        Double actualAccuracy = engineImpl.computeForecastAccuracy(new TimeSeriesAnalysisRequest(timeSeries, 1));

        verify(timeSeriesAnalysisEngineClient).computeForecastAccuracy(any());
        assertEquals(accuracy, actualAccuracy);
        assertEquals(WAITING, engineImpl.getState());
    }

    @Test
    public void should_modify_state_to_computing_and_then_waiting_on_compute_forecast_accuracy() {
        when(timeSeriesAnalysisEngineClient.computeForecastAccuracy(any())).thenReturn(accuracy);
        AtomicReference<EngineState> mockEngineState = mock(AtomicReference.class);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, mockEngineState);

        engineImpl.computeForecastAccuracy(timeSeriesAnalysisRequest);

        InOrder inOrder = inOrder(mockEngineState);
        inOrder.verify(mockEngineState).set(COMPUTING);
        inOrder.verify(mockEngineState).set(WAITING);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_append_predicted_values_to_initial_time_series_on_predict() {
        when(timeSeriesAnalysisEngineClient.predict(any())).thenReturn(timeSeries2);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, new AtomicReference<>(WAITING));

        TimeSeries actualTimeSeries = engineImpl.predict(new TimeSeriesAnalysisRequest(timeSeries, 1));

        TimeSeries expectedTimeSeries = new TimeSeries(asList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2), "Date", "Value", "%Y");
        verify(timeSeriesAnalysisEngineClient).predict(any());
        assertEquals(expectedTimeSeries, actualTimeSeries);
        assertEquals(WAITING, engineImpl.getState());
    }

    @Test
    public void should_modify_state_to_computing_and_then_waiting_on_predict() {
        when(timeSeriesAnalysisEngineClient.predict(any())).thenReturn(timeSeries2);
        AtomicReference<EngineState> mockEngineState = mock(AtomicReference.class);
        EngineImpl engineImpl = new EngineImpl(serviceInformation, engineClientFactory, mockEngineState);

        engineImpl.predict(timeSeriesAnalysisRequest);

        InOrder inOrder = inOrder(mockEngineState);
        inOrder.verify(mockEngineState).set(COMPUTING);
        inOrder.verify(mockEngineState).set(WAITING);
        inOrder.verifyNoMoreInteractions();
    }
}