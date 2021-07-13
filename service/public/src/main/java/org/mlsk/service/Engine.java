package org.mlsk.service;

import org.mlsk.service.model.EngineState;
import org.mlsk.service.model.ServiceInformation;

public interface Engine extends TimeSeriesAnalysis {

  EngineState getState();

  ServiceInformation getServiceInformation();
}
