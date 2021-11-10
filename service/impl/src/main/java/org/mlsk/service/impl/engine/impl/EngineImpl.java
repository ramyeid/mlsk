package org.mlsk.service.impl.engine.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;
import static org.mlsk.service.impl.setup.ServiceConfiguration.getEnginePath;
import static org.mlsk.service.impl.setup.ServiceConfiguration.getLogsPath;
import static org.mlsk.service.model.engine.EngineState.*;

public class EngineImpl implements Engine {

  private static final Logger LOGGER = LogManager.getLogger(EngineImpl.class);

  private final ResilientEngineProcess resilientEngineProcess;
  private final ServiceInformation serviceInformation;
  private final AtomicReference<EngineState> state;
  private final EngineClientFactory engineClientFactory;

  public EngineImpl(ServiceInformation serviceInformation) {
    this(new EngineClientFactory(), serviceInformation, new ResilientEngineProcess(serviceInformation, getLogsPath(), getEnginePath()), new AtomicReference<>(OFF));
  }

  @VisibleForTesting
  public EngineImpl(EngineClientFactory engineClientFactory, ServiceInformation serviceInformation, ResilientEngineProcess resilientEngineProcess, AtomicReference<EngineState> state) {
    this.resilientEngineProcess = resilientEngineProcess;
    this.serviceInformation = serviceInformation;
    this.state = state;
    this.engineClientFactory = engineClientFactory;
  }

  @Override
  public ServiceInformation getServiceInformation() {
    return serviceInformation;
  }

  @Override
  public EngineState getState() {
    return this.state.get();
  }

  @Override
  public synchronized void markAsWaitingForRequest() {
    this.state.set(WAITING);
  }

  @Override
  public synchronized void bookEngine() {
    this.state.set(BOOKED);
  }

  @Override
  public synchronized void markAsComputing() {
    this.state.set(COMPUTING);
  }

  @Override
  public synchronized void launchEngine() {
    if (this.state.get() == OFF || !this.resilientEngineProcess.isEngineUp()) {
      try {
        LOGGER.info("[Start] Launching engine: {}", this.serviceInformation);
        this.resilientEngineProcess.launchEngine(this::onEngineKilled);
        this.markAsWaitingForRequest();
      } catch (Exception exception) {
        LOGGER.error(format("Error while creating engine: %s", this.serviceInformation), exception);
        String message = format("Unable to launch engine %s", serviceInformation);
        this.state.set(OFF);
        throw new UnableToLaunchEngineException(message, exception);
      } finally {
        LOGGER.info("[End] Launching engine: {}", this.serviceInformation);
      }
    }
  }

  @Override
  public synchronized void onEngineKilled() {
    LOGGER.error("Engine {} died unexpectedly ", this.serviceInformation);
    LOGGER.info("Relaunching engine {}", this.serviceInformation);
    this.state.set(OFF);
    try {
      this.launchEngine();
    } catch (Exception exception) {
      LOGGER.error(format("Error while relaunching engine: %s", this.serviceInformation), exception);
    }
  }

  @Override
  public synchronized TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.forecast(timeSeriesAnalysisRequest);
  }

  @Override
  public synchronized Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
  }

  @Override
  public synchronized TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.predict(timeSeriesAnalysisRequest);
  }

  @Override
  public synchronized Void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType) {
    ClassifierEngineClient classifierEngineClient = engineClientFactory.buildClassifierEngineClient(serviceInformation);
    return classifierEngineClient.start(classifierStartRequest, classifierType);
  }

  @Override
  public synchronized Void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    ClassifierEngineClient classifierEngineClient = engineClientFactory.buildClassifierEngineClient(serviceInformation);
    return classifierEngineClient.data(classifierDataRequest, classifierType);
  }

  @Override
  public synchronized ClassifierDataResponse predict(ClassifierType classifierType) {
    ClassifierEngineClient classifierEngineClient = engineClientFactory.buildClassifierEngineClient(serviceInformation);
    return classifierEngineClient.predict(classifierType);
  }

  @Override
  public synchronized Double computePredictAccuracy(ClassifierType classifierType) {
    ClassifierEngineClient classifierEngineClient = engineClientFactory.buildClassifierEngineClient(serviceInformation);
    return classifierEngineClient.computePredictAccuracy(classifierType);
  }

  @Override
  public synchronized Void cancel(ClassifierType classifierType) {
    ClassifierEngineClient classifierEngineClient = engineClientFactory.buildClassifierEngineClient(serviceInformation);
    return classifierEngineClient.cancel(classifierType);
  }
}
