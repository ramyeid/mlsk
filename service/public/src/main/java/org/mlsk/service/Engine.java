package org.mlsk.service;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.model.EngineState;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;

public interface Engine extends TimeSeriesAnalysisEngine {

  void launchEngine();

  void bookEngine();

  void onEngineKilled();

  EngineState getState();

  ServiceInformation getServiceInformation();
}
