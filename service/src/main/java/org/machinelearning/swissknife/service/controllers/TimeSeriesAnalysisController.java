package org.machinelearning.swissknife.service.controllers;

import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.Orchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.machinelearning.swissknife.lib.algorithms.AlgorithmNames.*;
import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

import org.apache.log4j.Logger;

@RestController
public class TimeSeriesAnalysisController implements TimeSeriesAnalysis {

    private static final Logger LOGGER = Logger.getLogger(TimeSeriesAnalysisController.class);

    private final Orchestrator orchestrator;

    @Autowired
    public TimeSeriesAnalysisController(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping(FORECAST_URL)
    public TimeSeries forecast(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            LOGGER.info("[Start] forecast request");
            return orchestrator.runOnEngine(engine -> engine.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception while to forecasting: %s", exception.getMessage()), exception);
            throw exception;
        } finally {
            LOGGER.info("[End] forecast request");
        }
    }

    @PostMapping(FORECAST_ACCURACY_URL)
    public Double computeForecastAccuracy(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            LOGGER.info("[Start] compute forecast accuracy request");
            return orchestrator.runOnEngine(engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception while to computing forecast accuracy: %s", exception.getMessage()), exception);
            throw exception;
        } finally {
            LOGGER.info("[End] compute forecast accuracy request");
        }
    }

    @PostMapping(PREDICATE_URL)
    public TimeSeries predict(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            LOGGER.info("[Start] predict request");
            return orchestrator.runOnEngine(engine -> engine.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception while to predicting: %s", exception.getMessage()), exception);
            throw exception;
        } finally {
            LOGGER.info("[End] predict request");
        }
    }
}
