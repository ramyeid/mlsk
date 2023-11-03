package org.mlsk.service.impl.engine.client;

import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(Endpoint endpoint) {
    return new TimeSeriesAnalysisEngineClient(endpoint);
  }

  public ClassifierEngineClient buildClassifierEngineClient(Endpoint endpoint) {
    return new ClassifierEngineClient(endpoint);
  }
}
