package org.machinelearning.swissknife.service.engine;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesRow;
import org.machinelearning.swissknife.service.controllers.TimeSeriesAnalysisController;
import org.machinelearning.swissknife.service.engine.client.EngineClientFactory;
import org.machinelearning.swissknife.service.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.machinelearning.swissknife.service.engine.process.ResilientProcess;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.lib.algorithms.AlgorithmNames.*;
import static org.machinelearning.swissknife.model.EngineState.COMPUTING;
import static org.machinelearning.swissknife.model.EngineState.WAITING;

public class EngineImpl implements Engine {

    private static final Logger LOGGER = Logger.getLogger(TimeSeriesAnalysisController.class);

    private ResilientProcess engineProcess;
    private final ServiceInformation serviceInformation;
    private final EngineClientFactory engineClientFactory;
    private final AtomicReference<EngineState> state;

    public EngineImpl(ServiceInformation serviceInformation) throws IOException, InterruptedException {
        this.serviceInformation = serviceInformation;
        this.engineClientFactory = new EngineClientFactory();
        this.state = new AtomicReference<>(WAITING);
        this.engineProcess = new ResilientProcess(serviceInformation, this::onProcessKilled);
    }

    @VisibleForTesting
    public EngineImpl(ServiceInformation serviceInformation, EngineClientFactory engineClientFactory, AtomicReference<EngineState> state, ResilientProcess resilientProcess) throws IOException, InterruptedException {
        this.serviceInformation = serviceInformation;
        this.engineClientFactory = engineClientFactory;
        this.state = state;
        this.engineProcess = resilientProcess;
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
    public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return callOnEngine(() -> {
            TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
            int numberOfValues = timeSeriesAnalysisRequest.getNumberOfValues();
            TimeSeries timeSeries = timeSeriesAnalysisRequest.getTimeSeries();
            List<TimeSeriesRow> rows = timeSeries.getRows();

            List<TimeSeriesRow> newRows = rows.subList(0, rows.size() - numberOfValues);
            TimeSeries newTimeSeries = new TimeSeries(newRows, timeSeries.getDateColumnName(), timeSeries.getValueColumnName(), timeSeries.getDateFormat());
            TimeSeriesAnalysisRequest newTimeSeriesRequest = new TimeSeriesAnalysisRequest(newTimeSeries, numberOfValues);

            TimeSeries forecastedValues = engineClient.forecast(newTimeSeriesRequest);
            return TimeSeries.concat(newTimeSeries, forecastedValues);
        }, TIME_SERIES_FORECAST_VS_ACTUAL);
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
            LOGGER.info(String.format("Engine %s computing request: %s", this.serviceInformation, actionName));
            this.state.set(COMPUTING);
            return supplier.get();
        } catch (Exception exception) {
            LOGGER.error(String.format("Error while launching: %s on engine %s: %s", actionName, serviceInformation, exception.getMessage()));
            throw exception;
        } finally {
            this.state.set(WAITING);
            LOGGER.info(String.format("Engine %s in waiting mode after computing request: %s", this.serviceInformation, actionName));
        }
    }

    @VisibleForTesting
    void onProcessKilled() {
        LOGGER.error(String.format("Engine %s died unexpectedly", this.serviceInformation));
        LOGGER.info(String.format("[Start] Relaunching engine %s", this.serviceInformation));
        this.state.set(EngineState.OFF);
        try {
            engineProcess.launchProcess();
            LOGGER.info(String.format("Engine %s relaunched succesfully", this.serviceInformation));
            this.state.set(EngineState.WAITING);
        } catch (Exception exception) {
            LOGGER.error(String.format("Error while relaunching engine: %s", this.serviceInformation), exception);
        } finally {
            LOGGER.info(String.format("[End] Relaunching engine %s", this.serviceInformation));
        }
    }
}
