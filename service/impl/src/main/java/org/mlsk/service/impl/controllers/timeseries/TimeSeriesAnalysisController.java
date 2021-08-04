package org.mlsk.service.impl.controllers.timeseries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.TimeSeriesAnalysis;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.exceptions.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.mlsk.service.utils.TimeSeriesAnalysisAlgorithmNames.*;
import static org.mlsk.service.utils.TimeSeriesAnalysisUrls.*;

@RestController
public class TimeSeriesAnalysisController implements TimeSeriesAnalysis {

  private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAnalysisController.class);

  private final Orchestrator orchestrator;

  @Autowired
  public TimeSeriesAnalysisController(Orchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @PostMapping(FORECAST_URL)
  public TimeSeries forecast(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] forecast request");
      return orchestrator.runOnEngine(engine -> engine.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while forecasting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] forecast request");
    }
  }

  @PostMapping(FORECAST_VS_ACTUAL_URL)
  public TimeSeries forecastVsActual(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] forecast vs actual request");
      return orchestrator.runOnEngine(engine -> engine.forecastVsActual(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_VS_ACTUAL);
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while forecasting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] forecast vs actual request");
    }
  }

  @PostMapping(FORECAST_ACCURACY_URL)
  public Double computeForecastAccuracy(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] compute forecast accuracy request");
      return orchestrator.runOnEngine(engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while computing forecast accuracy: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] compute forecast accuracy request");
    }
  }

  @PostMapping(PREDICATE_URL)
  public TimeSeries predict(@RequestBody TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] predict request");
      return orchestrator.runOnEngine(engine -> engine.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
    } catch (Exception exception) {
      LOGGER.error(String.format("Exception while predicting: %s", exception.getMessage()), exception);
      throw new TimeSeriesAnalysisServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] predict request");
    }
  }
}
