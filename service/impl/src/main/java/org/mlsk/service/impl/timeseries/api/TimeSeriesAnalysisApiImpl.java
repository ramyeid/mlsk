package org.mlsk.service.impl.timeseries.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.service.impl.timeseries.service.TimeSeriesAnalysisServiceImpl;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesAnalysisRequestMapper.toTimeSeriesAnalysisRequest;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesMapper.toTimeSeriesModel;

@RestController
public class TimeSeriesAnalysisApiImpl implements TimeSeriesAnalysisApi {

  private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAnalysisApiImpl.class);

  private final TimeSeriesAnalysisServiceImpl service;

  @Autowired
  public TimeSeriesAnalysisApiImpl(TimeSeriesAnalysisServiceImpl service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecast(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    LOGGER.info("Forecast request received");
    TimeSeries result = service.forecast(toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecastVsActual(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    LOGGER.info("Forecast vs Actual request received");
    TimeSeries result = service.forecastVsActual(toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }

  @Override
  public ResponseEntity<BigDecimal> computeForecastAccuracy(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    LOGGER.info("Compute Forecast Accuracy request received");
    Double result = service.computeForecastAccuracy(toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(valueOf(result));
  }

  @Override
  public ResponseEntity<TimeSeriesModel> predict(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    LOGGER.info("Predict request received");
    TimeSeries result = service.predict(toTimeSeriesAnalysisRequest(timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }
}
