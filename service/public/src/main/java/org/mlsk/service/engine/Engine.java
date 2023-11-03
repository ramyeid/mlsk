package org.mlsk.service.engine;

import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;

public interface Engine extends TimeSeriesAnalysisEngine, ClassifierEngine {

  void launchEngine();

  void markAsWaitingForRequest();

  void bookEngine();

  void markAsComputing();

  void onEngineKilled();

  EngineState getState();

  Endpoint getEndpoint();
}
