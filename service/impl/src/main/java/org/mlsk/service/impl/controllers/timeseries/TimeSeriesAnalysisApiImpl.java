package org.mlsk.service.impl.controllers.timeseries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesAnalysisRequestMapper.toTimeSeriesAnalysisRequest;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesMapper.toTimeSeriesModel;
import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class TimeSeriesAnalysisApiImpl implements TimeSeriesAnalysisApi {

  private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAnalysisApiImpl.class);

  private final Orchestrator orchestrator;

  @Autowired
  public TimeSeriesAnalysisApiImpl(Orchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecast(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    try {
      LOGGER.info("[Start] forecast request");
      TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel);
      TimeSeries result = orchestrator.runOnEngine(engine -> engine.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
      return ResponseEntity.status(OK).body(toTimeSeriesModel(result));
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while forecasting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] forecast request");
    }
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecastVsActual(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    try {
      LOGGER.info("[Start] forecast vs actual request");
      TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel);
      TimeSeries result = orchestrator.runOnEngine(engine -> engine.forecastVsActual(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_VS_ACTUAL);
      return ResponseEntity.status(OK).body(toTimeSeriesModel(result));
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while forecasting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] forecast vs actual request");
    }
  }

  @Override
  public ResponseEntity<BigDecimal> computeForecastAccuracy(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    try {
      LOGGER.info("[Start] compute forecast accuracy request");
      TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel);
      Double result = orchestrator.runOnEngine(engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
      return ResponseEntity.status(OK).body(valueOf(result));
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while computing forecast accuracy: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] compute forecast accuracy request");
    }
  }

  @Override
  public ResponseEntity<TimeSeriesModel> predict(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    try {
      LOGGER.info("[Start] predict request");
      TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel);
      TimeSeries result = orchestrator.runOnEngine(engine -> engine.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
      return ResponseEntity.status(OK).body(toTimeSeriesModel(result));
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while predicting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] predict request");
    }
  }
}
