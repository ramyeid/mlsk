package org.mlsk.service.impl.engine.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.classifier.*;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;
import static org.mlsk.service.impl.setup.ServiceConfiguration.*;
import static org.mlsk.service.model.engine.EngineState.*;

public class EngineImpl implements Engine {

  private static final Logger LOGGER = LogManager.getLogger(EngineImpl.class);

  private final ResilientEngineProcess resilientEngineProcess;
  private final Endpoint endpoint;
  private final AtomicReference<EngineState> state;
  private final TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient;
  private final ClassifierEngineClient classifierEngineClient;

  public EngineImpl(Endpoint endpoint) {
    this(new EngineClientFactory(), endpoint, new ResilientEngineProcess(endpoint, getLogsPath(), getEnginePath(), getEngineLogLevel()), new AtomicReference<>(OFF));
  }

  @VisibleForTesting
  public EngineImpl(EngineClientFactory engineClientFactory, Endpoint endpoint, ResilientEngineProcess resilientEngineProcess, AtomicReference<EngineState> state) {
    this.resilientEngineProcess = resilientEngineProcess;
    this.endpoint = endpoint;
    this.state = state;
    this.timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(endpoint, engineClientFactory);
    this.classifierEngineClient = new ClassifierEngineClient(endpoint, engineClientFactory);
  }

  @Override
  public Endpoint getEndpoint() {
    return endpoint;
  }

  @Override
  public EngineState getState() {
    return this.state.get();
  }

  @Override
  public void markAsNotAvailable() {
    this.state.set(OFF);
  }

  @Override
  public void markAsReadyForNewRequest() {
    this.state.set(IDLE);
  }

  @Override
  public boolean markAsBooked() {
    return this.state.compareAndSet(IDLE, BOOKED);
  }

  @Override
  public void markAsStartingAction() {
    this.state.set(COMPUTING);
  }

  @Override
  public void markAsActionEnded() {
    this.state.set(BOOKED);
  }

  @Override
  public void launchEngine(Runnable onEngineKilled) {
    if (this.state.get() == OFF || !this.resilientEngineProcess.isEngineUp()) {
      try {
        LOGGER.info("[Start] Launching engine: {}", this.endpoint);
        this.resilientEngineProcess.launchEngine(onEngineKilled);
      } catch (Exception exception) {
        LOGGER.error(format("Error while creating engine: %s", this.endpoint), exception);
        String message = format("Unable to launch engine %s", endpoint);
        throw new UnableToLaunchEngineException(message, exception);
      } finally {
        LOGGER.info("[End] Launching engine: {}", this.endpoint);
      }
    }
  }

  @Override
  public void start(ClassifierStartRequest classifierStartRequest) {
    this.classifierEngineClient.start(classifierStartRequest);
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest) {
    this.classifierEngineClient.data(classifierDataRequest);
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest) {
    return this.classifierEngineClient.predict(classifierRequest);
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest) {
    return this.classifierEngineClient.computePredictAccuracy(classifierRequest);
  }

  @Override
  public void cancel(ClassifierCancelRequest classifierCancelRequest) {
    this.classifierEngineClient.cancel(classifierCancelRequest);
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return this.timeSeriesAnalysisEngineClient.forecast(timeSeriesAnalysisRequest);
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return this.timeSeriesAnalysisEngineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return this.timeSeriesAnalysisEngineClient.predict(timeSeriesAnalysisRequest);
  }
}
