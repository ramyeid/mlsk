package org.mlsk.service.impl.engine.client;

import org.mlsk.service.impl.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.ServiceInformation;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
    return new TimeSeriesAnalysisEngineClient(serviceInformation);
  }
}
