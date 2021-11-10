package org.mlsk.service.impl.timeseries.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.timeseries.service.exception.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.mlsk.service.timeseries.utils.TimeSeriesAnalysisConstants.*;

@Service
public class TimeSeriesAnalysisServiceImpl implements TimeSeriesAnalysisService {

  private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAnalysisServiceImpl.class);

  private final Orchestrator orchestrator;

  @Autowired
  public TimeSeriesAnalysisServiceImpl(Orchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] forecast request");
      return orchestrator.runOnEngine(engine -> engine.forecast(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST);
    } catch (Exception exception) {
      throw logAndBuildException(exception, "forecasting");
    } finally {
      LOGGER.info("[End] forecast request");
    }
  }

  @Override
  public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] forecast vs actual request");
      TimeSeriesAnalysisRequest newRequest = removeLastRows(timeSeriesAnalysisRequest);
      return orchestrator.runOnEngine(engine -> engine.forecast(newRequest), TIME_SERIES_FORECAST_VS_ACTUAL);
    } catch (Exception exception) {
      throw logAndBuildException(exception, "forecasting");
    } finally {
      LOGGER.info("[End] forecast vs actual request");
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] compute forecast accuracy request");
      return orchestrator.runOnEngine(engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest), TIME_SERIES_FORECAST_ACCURACY);
    } catch (Exception exception) {
      throw logAndBuildException(exception, "computing forecast accuracy");
    } finally {
      LOGGER.info("[End] compute forecast accuracy request");
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    try {
      LOGGER.info("[Start] predict request");
      return orchestrator.runOnEngine(engine -> engine.predict(timeSeriesAnalysisRequest), TIME_SERIES_PREDICT);
    } catch (Exception exception) {
      throw logAndBuildException(exception, "predicting");
    } finally {
      LOGGER.info("[End] predict request");
    }
  }

  private static TimeSeriesAnalysisRequest removeLastRows(TimeSeriesAnalysisRequest request) {
    int numberOfValues = request.getNumberOfValues();
    TimeSeries timeSeries = request.getTimeSeries();
    List<TimeSeriesRow> rows = timeSeries.getRows();

    List<TimeSeriesRow> newRows = rows.subList(0, rows.size() - numberOfValues);
    TimeSeries newTimeSeries = new TimeSeries(newRows, timeSeries.getDateColumnName(), timeSeries.getValueColumnName(), timeSeries.getDateFormat());
    return new TimeSeriesAnalysisRequest(newTimeSeries, numberOfValues);
  }

  private static TimeSeriesAnalysisServiceException logAndBuildException(Exception exception, String action) {
    LOGGER.error(format("Exception while %s: %s", action, exception.getMessage()), exception);
    return new TimeSeriesAnalysisServiceException(exception.getMessage());
  }
}
