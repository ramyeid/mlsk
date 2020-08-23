package org.machinelearning.swissknife.service.engine;

import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.service.engine.timeseries.TimeSeriesAnalysisEngineClient;

public class EngineClientFactory {

    public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
        return new TimeSeriesAnalysisEngineClient(serviceInformation);
    }
}
