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
import static org.mlsk.service.model.timeseries.utils.TimeSeriesAnalysisConstants.*;

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
    long requestId = timeSeriesAnalysisRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] forecast request", requestId);
      return orchestrator.bookEngineRunAndComplete(requestId, TIME_SERIES_FORECAST, engine -> engine.forecast(timeSeriesAnalysisRequest));
    } catch (Exception exception) {
      throw logAndBuildException(exception, requestId, "forecasting");
    } finally {
      LOGGER.info("[End][{}] forecast request", requestId);
    }
  }

  @Override
  public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    long requestId = timeSeriesAnalysisRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] forecast vs actual request", requestId);
      TimeSeriesAnalysisRequest newRequest = removeLastRows(timeSeriesAnalysisRequest);
      return orchestrator.bookEngineRunAndComplete(requestId, TIME_SERIES_FORECAST_VS_ACTUAL, engine -> engine.forecast(newRequest));
    } catch (Exception exception) {
      throw logAndBuildException(exception, requestId, "forecasting");
    } finally {
      LOGGER.info("[End][{}] forecast vs actual request", requestId);
    }
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    long requestId = timeSeriesAnalysisRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] compute forecast accuracy request", requestId);
      return orchestrator.bookEngineRunAndComplete(requestId, TIME_SERIES_FORECAST_ACCURACY, engine -> engine.computeForecastAccuracy(timeSeriesAnalysisRequest));
    } catch (Exception exception) {
      throw logAndBuildException(exception, requestId, "computing forecast accuracy");
    } finally {
      LOGGER.info("[End][{}] compute forecast accuracy request", requestId);
    }
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    long requestId = timeSeriesAnalysisRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] predict request", requestId);
      return orchestrator.bookEngineRunAndComplete(requestId, TIME_SERIES_PREDICT, engine -> engine.predict(timeSeriesAnalysisRequest));
    } catch (Exception exception) {
      throw logAndBuildException(exception, requestId, "predicting");
    } finally {
      LOGGER.info("[End][{}] predict request", requestId);
    }
  }

  private static TimeSeriesAnalysisRequest removeLastRows(TimeSeriesAnalysisRequest request) {
    int numberOfValues = request.getNumberOfValues();
    TimeSeries timeSeries = request.getTimeSeries();
    List<TimeSeriesRow> rows = timeSeries.getRows();

    List<TimeSeriesRow> newRows = rows.subList(0, rows.size() - numberOfValues);
    TimeSeries newTimeSeries = new TimeSeries(newRows, timeSeries.getDateColumnName(), timeSeries.getValueColumnName(), timeSeries.getDateFormat());
    return new TimeSeriesAnalysisRequest(request.getRequestId(), newTimeSeries, numberOfValues);
  }

  private static TimeSeriesAnalysisServiceException logAndBuildException(Exception exception, long requestId, String action) {
    LOGGER.error(format("[%d] Exception while %s: %s", requestId, action, exception.getMessage()), exception);
    return new TimeSeriesAnalysisServiceException(exception.getMessage());
  }
}
