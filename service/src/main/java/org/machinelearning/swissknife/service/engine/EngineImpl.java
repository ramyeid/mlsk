package org.machinelearning.swissknife.service.engine;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.controllers.TimeSeriesAnalysisController;
import org.machinelearning.swissknife.service.engine.timeseries.TimeSeriesAnalysisEngineClient;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.lib.algorithms.AlgorithmNames.*;
import static org.machinelearning.swissknife.model.EngineState.COMPUTING;
import static org.machinelearning.swissknife.model.EngineState.WAITING;

public class EngineImpl implements Engine {

    private static final Logger LOGGER = Logger.getLogger(TimeSeriesAnalysisController.class);

    private final ServiceInformation serviceInformation;
    private final EngineClientFactory engineClientFactory;
    private final AtomicReference<EngineState> state;

    public EngineImpl(ServiceInformation serviceInformation) {
        this(serviceInformation, new EngineClientFactory(), new AtomicReference<>(WAITING));
    }

    @VisibleForTesting
    public EngineImpl(ServiceInformation serviceInformation, EngineClientFactory engineClientFactory, AtomicReference<EngineState> state) {
        this.serviceInformation = serviceInformation;
        this.engineClientFactory = engineClientFactory;
        this.state = state;
    }

    @Override
    public synchronized EngineState getState() {
        return this.state.get();
    }


    @Override
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }

    @Override
    public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            TimeSeries forecastedValues = engineClient.forecast(timeSeriesAnalysisRequest);
            return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), forecastedValues);
        }, TIME_SERIES_FORECAST);
    }

    @Override
    public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            return engineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
        }, TIME_SERIES_FORECAST_ACCURACY);
    }

    @Override
    public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            TimeSeries predictedValues = engineClient.predict(timeSeriesAnalysisRequest);
            return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), predictedValues);
        }, TIME_SERIES_PREDICT);
    }

    private <Result> Result callOnEngine(Supplier<Result> supplier, String actionName) {
        try {
            LOGGER.info(String.format("Engine %s computing request: %s" , this.serviceInformation, actionName));
            this.state.set(COMPUTING);
            return supplier.get();
        } catch(Exception exception) {
            LOGGER.error(String.format("Error while launching: %s on engine %s: %s", actionName, serviceInformation, exception.getMessage()));
            throw exception;
        } finally {
            this.state.set(WAITING);
            LOGGER.info(String.format("Engine %s in waiting mode after computing request: %s" , this.serviceInformation, actionName));
        }
    }
}
