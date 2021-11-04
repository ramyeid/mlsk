package org.mlsk.service.impl.engine.client;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineClient buildTimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
    return new TimeSeriesAnalysisEngineClient(serviceInformation);
  }

  public ClassifierEngineClient buildClassifierEngineClient(ServiceInformation serviceInformation) {
    return new ClassifierEngineClient(serviceInformation);
  }
}
