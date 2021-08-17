package org.mlsk.service.engine;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;

public interface Engine extends TimeSeriesAnalysisEngine {

  void launchEngine();

  void bookEngine();

  void onEngineKilled();

  EngineState getState();

  ServiceInformation getServiceInformation();
}
