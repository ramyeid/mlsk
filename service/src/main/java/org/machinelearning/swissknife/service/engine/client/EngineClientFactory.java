package org.machinelearning.swissknife.service.engine.client;

import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.service.engine.client.timeseries.TimeSeriesAnalysisEngineClient;

public class EngineClientFactory {

    public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
        return new TimeSeriesAnalysisEngineClient(serviceInformation);
    }
}
