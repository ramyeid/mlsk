package org.mlsk.service.engine;

import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.admin.AdminEngine;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.timeseries.TimeSeriesAnalysisEngine;

public interface Engine extends TimeSeriesAnalysisEngine, ClassifierEngine, AdminEngine {

  void launchEngine(Runnable onEngineKilled);

  EngineState getState();

  Endpoint getEndpoint();

  void markAsNotAvailable();

  void markAsReadyForNewRequest();

  boolean markAsBooked();

  void markAsStartingAction();

  void markAsActionEnded();
}
