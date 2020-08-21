package org.machinelearning.swissknife.service.controllers;

import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.engine.client.TimeSeriesAnalysisEngineClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

@RestController
public class TimeSeriesAnalysisController implements TimeSeriesAnalysis {

    @PostMapping(FORECAST_URL)
    public TimeSeries forecast(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        ServiceInformation serviceInformation = new ServiceInformation("localhost", "6767");
        TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(serviceInformation);
        TimeSeries timeSeriesWithForecastedValues =  timeSeriesAnalysisEngineClient.forecast(timeSeriesAnalysisRequest);
        return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), timeSeriesWithForecastedValues);

    }

    @PostMapping(FORECAST_ACCURACY_URL)
    public Double computeForecastAccuracy(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        ServiceInformation serviceInformation = new ServiceInformation("localhost", "6767");
        TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(serviceInformation);
        return timeSeriesAnalysisEngineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
    }

    @PostMapping(PREDICATE_URL)
    public TimeSeries predict(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        ServiceInformation serviceInformation = new ServiceInformation("localhost", "6767");
        TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(serviceInformation);
        TimeSeries timeSeriesWithPredicatedValues =  timeSeriesAnalysisEngineClient.predict(timeSeriesAnalysisRequest);
        return TimeSeries.concat(timeSeriesAnalysisRequest.getTimeSeries(), timeSeriesWithPredicatedValues);

    }
}
