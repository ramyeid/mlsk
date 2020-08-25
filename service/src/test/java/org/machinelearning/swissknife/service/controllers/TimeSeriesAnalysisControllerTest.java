package org.machinelearning.swissknife.service.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.Orchestrator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.machinelearning.swissknife.lib.algorithms.AlgorithmNames.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSeriesAnalysisControllerTest {

    @Mock
    private Orchestrator orchestrator;
    @Mock
    private Engine engine;
    private TimeSeriesAnalysisController controller;

    @BeforeEach
    public void setup() {
        this.controller = new TimeSeriesAnalysisController(orchestrator);
        when(orchestrator.runOnEngine(any(), any()))
                .thenAnswer(invocation -> {
                    Function<Engine, ?> function = (Function<Engine, ?>) invocation.getArguments()[0];
                    return function.apply(engine);
                });
    }

    @Test
    public void should_delegate_call_to_orchestrator_and_engine_on_forecast() {
        TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);

        controller.forecast(mock);

        verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST));
        verify(engine).forecast(mock);
    }

    @Test
    public void should_delegate_call_to_orchestrator_and_engine_on_compute_forecast_accuracy() {
        TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);

        controller.computeForecastAccuracy(mock);

        verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_FORECAST_ACCURACY));
        verify(engine).computeForecastAccuracy(mock);
    }

    @Test
    public void should_delegate_call_to_orchestrator_and_engine_on_predict() {
        TimeSeriesAnalysisRequest mock = mock(TimeSeriesAnalysisRequest.class);

        controller.predict(mock);

        verify(orchestrator).runOnEngine(any(), eq(TIME_SERIES_PREDICT));
        verify(engine).predict(mock);
    }
}