package org.mlsk.service.impl.engine.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.exception.UnableToLaunchEngineException;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.model.engine.EngineState;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.getEnginePath;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.getLogsPath;
import static org.mlsk.service.model.engine.EngineState.*;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;

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
  public synchronized void bookEngine() {
    this.state.set(BOOKED);
  }

  @Override
  public synchronized void launchEngine() {
    if (this.state.get() == OFF || !this.resilientEngineProcess.isEngineUp()) {
      try {
        LOGGER.info("[Start] Launching engine: {}", this.serviceInformation);
        this.resilientEngineProcess.launchEngine(this::onEngineKilled);
        this.state.set(WAITING);
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
    return callOnEngine(() -> engineClient.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
  }

  @Override
  public synchronized Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return callOnEngine(() -> engineClient.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
  }

  @Override
  public synchronized TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return callOnEngine(() -> engineClient.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
  }

  private <Result> Result callOnEngine(Supplier<Result> supplier, String actionName) {
    try {
      LOGGER.info("Engine {} computing request: {}", this.serviceInformation, actionName);
      this.state.set(COMPUTING);
      return supplier.get();
    } catch (Exception exception) {
      LOGGER.error("Error while launching: {} on engine {}: {}", actionName, serviceInformation, exception.getMessage());
      throw exception;
    } finally {
      this.state.set(WAITING);
      LOGGER.info("Engine {} in waiting mode after computing request: {}", this.serviceInformation, actionName);
    }
  }
}
