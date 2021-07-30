package org.mlsk.service;

import org.mlsk.service.model.EngineState;
import org.mlsk.lib.model.ServiceInformation;

public interface Engine extends TimeSeriesAnalysis {

  void bookEngine();

  EngineState getState();

  ServiceInformation getServiceInformation();

  void onProcessKilled();
}
