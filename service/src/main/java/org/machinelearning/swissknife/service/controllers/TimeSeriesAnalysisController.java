package org.machinelearning.swissknife.service.controllers;

import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.Orchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

@RestController
public class TimeSeriesAnalysisController implements TimeSeriesAnalysis {

    private final Orchestrator orchestrator;

    @Autowired
    public TimeSeriesAnalysisController(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping(FORECAST_URL)
    public TimeSeries forecast(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return orchestrator.runOnEngine(engine -> engine.forecast(timeSeriesAnalysisRequest), "time-series-forecast");
    }

    @PostMapping(FORECAST_ACCURACY_URL)
    public Double computeForecastAccuracy(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return orchestrator.runOnEngine(engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest), "time-series-compute-accuracy");
    }

    @PostMapping(PREDICATE_URL)
    public TimeSeries predict(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        return orchestrator.runOnEngine(engine -> engine.predict(timeSeriesAnalysisRequest), "time-series-predict");
    }
}
