package org.mlsk.service.impl.engine.client;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
    return new TimeSeriesAnalysisEngineClient(serviceInformation);
  }
}
