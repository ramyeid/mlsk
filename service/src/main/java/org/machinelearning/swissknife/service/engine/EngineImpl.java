package org.machinelearning.swissknife.service.engine;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.EngineState;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.engine.timeseries.TimeSeriesAnalysisEngineClient;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.EngineState.COMPUTING;
import static org.machinelearning.swissknife.EngineState.WAITING;

public class EngineImpl implements Engine {

    private final ServiceInformation serviceInformation;
    private final EngineClientFactory engineClientFactory;
    private final AtomicReference<EngineState> state;

    public EngineImpl(ServiceInformation serviceInformation) {
        this(serviceInformation, new EngineClientFactory(), new AtomicReference<>(WAITING));
    }

    @Override
    public synchronized EngineState getState() {
        return this.state.get();
    }

    @VisibleForTesting
    public EngineImpl(ServiceInformation serviceInformation, EngineClientFactory engineClientFactory, AtomicReference<EngineState> state) {
        this.serviceInformation = serviceInformation;
        this.engineClientFactory = engineClientFactory;
        this.state = state;
    }

    @Override
    public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            TimeSeries forecastedValues = engineClient.forecast(timeSeriesAnalysisRequest);
            return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), forecastedValues);
        });
    }

    @Override
    public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            return engineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
        });
    }

    @Override
    public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            TimeSeries predictedValues = engineClient.predict(timeSeriesAnalysisRequest);
            return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), predictedValues);
        });
    }

    private <Result> Result callOnEngine(Supplier<Result> supplier) {
        this.state.set(COMPUTING);
        Result result = supplier.get();
        this.state.set(WAITING);
        return result;
    }
}
