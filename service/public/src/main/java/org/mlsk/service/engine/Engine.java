package org.mlsk.service.engine;

import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;

public interface Engine extends TimeSeriesAnalysisEngine, ClassifierEngine {

  void launchEngine();

  void markAsWaiting();

  void bookEngine();

  void markAsComputing();

  void onEngineKilled();

  EngineState getState();

  ServiceInformation getServiceInformation();
}
