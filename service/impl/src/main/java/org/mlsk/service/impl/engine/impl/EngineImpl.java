package org.mlsk.service.impl.engine.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.ServiceConfiguration;
import org.mlsk.service.impl.engine.impl.timeseries.TimeSeriesAnalysisEngineCaller;
import org.mlsk.lib.engine.ResilientEngine;
import org.mlsk.service.model.EngineState;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.mlsk.service.impl.ServiceConfiguration.getEnginePath;
import static org.mlsk.service.impl.ServiceConfiguration.getLogsPath;
import static org.mlsk.service.model.EngineState.COMPUTING;
import static org.mlsk.service.model.EngineState.WAITING;
import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.*;

public class EngineImpl implements Engine {

  private static final Logger LOGGER = Logger.getLogger(EngineImpl.class);

  private final ResilientEngine engineProcess;
  private final ServiceInformation serviceInformation;
  private final AtomicReference<EngineState> state;
  private final TimeSeriesAnalysisEngineCaller timeSeriesAnalysisEngineCaller;

  public EngineImpl(ServiceInformation serviceInformation) throws IOException, InterruptedException {
    this.serviceInformation = serviceInformation;
    this.state = new AtomicReference<>(WAITING);
    this.engineProcess = new ResilientEngine(serviceInformation, getLogsPath(), getEnginePath(), this::onProcessKilled);
    this.timeSeriesAnalysisEngineCaller = new TimeSeriesAnalysisEngineCaller(serviceInformation);
  }

  @VisibleForTesting
  public EngineImpl(ResilientEngine engineProcess, ServiceInformation serviceInformation, AtomicReference<EngineState> state, TimeSeriesAnalysisEngineCaller timeSeriesAnalysisEngineCaller) {
    this.engineProcess = engineProcess;
    this.serviceInformation = serviceInformation;
    this.state = state;
    this.timeSeriesAnalysisEngineCaller = timeSeriesAnalysisEngineCaller;
  }

  @Override
  public synchronized EngineState getState() {
    return this.state.get();
  }

  @Override
  public void bookEngine() {
    this.state.set(EngineState.BOOKED);
  }

  @Override
  public ServiceInformation getServiceInformation() {
    return serviceInformation;
  }

  public void onProcessKilled() {
    LOGGER.error(String.format("Engine %s died unexpectedly", this.serviceInformation));
    LOGGER.info(String.format("[Start] Relaunching engine %s", this.serviceInformation));
    this.state.set(EngineState.OFF);
    try {
      engineProcess.launchEngine();
      LOGGER.info(String.format("Engine %s relaunched succesfully", this.serviceInformation));
      this.state.set(EngineState.WAITING);
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while relaunching engine: %s", this.serviceInformation), exception);
    } finally {
      LOGGER.info(String.format("[End] Relaunching engine %s", this.serviceInformation));
    }
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return callOnEngine(() -> timeSeriesAnalysisEngineCaller.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
  }

  @Override
  public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return callOnEngine(() -> timeSeriesAnalysisEngineCaller.forecastVsActual(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_VS_ACTUAL);
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return callOnEngine(() -> timeSeriesAnalysisEngineCaller.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return callOnEngine(() -> timeSeriesAnalysisEngineCaller.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
  }

  private <Result> Result callOnEngine(Supplier<Result> supplier, String actionName) {
    try {
      LOGGER.info(String.format("Engine %s computing request: %s", this.serviceInformation, actionName));
      this.state.set(COMPUTING);
      return supplier.get();
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while launching: %s on engine %s: %s", actionName, serviceInformation, exception.getMessage()));
      throw exception;
    } finally {
      this.state.set(WAITING);
      LOGGER.info(String.format("Engine %s in waiting mode after computing request: %s", this.serviceInformation, actionName));
    }
  }
}
