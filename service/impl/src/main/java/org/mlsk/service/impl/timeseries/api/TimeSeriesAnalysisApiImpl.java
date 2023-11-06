package org.mlsk.service.impl.timeseries.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.service.timeseries.api.TimeSeriesAnalysisApi;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.timeseries.TimeSeriesAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.impl.timeseries.api.mapper.TimeSeriesAnalysisRequestMapper.toTimeSeriesAnalysisRequest;
import static org.mlsk.service.impl.timeseries.api.mapper.TimeSeriesMapper.toTimeSeriesModel;

@RestController
public class TimeSeriesAnalysisApiImpl implements TimeSeriesAnalysisApi {

  private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAnalysisApiImpl.class);

  private final TimeSeriesAnalysisService service;

  @Autowired
  public TimeSeriesAnalysisApiImpl(TimeSeriesAnalysisService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecast(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    long requestId = RequestIdGenerator.nextId();
    LOGGER.info("[{}] Forecast request received", requestId);
    TimeSeries result = service.forecast(toTimeSeriesAnalysisRequest(requestId, timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }

  @Override
  public ResponseEntity<TimeSeriesModel> forecastVsActual(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    long requestId = RequestIdGenerator.nextId();
    LOGGER.info("[{}] Forecast vs Actual request received", requestId);
    TimeSeries result = service.forecastVsActual(toTimeSeriesAnalysisRequest(requestId, timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }

  @Override
  public ResponseEntity<BigDecimal> computeForecastAccuracy(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    long requestId = RequestIdGenerator.nextId();
    LOGGER.info("[{}] Compute Forecast Accuracy request received", requestId);
    Double result = service.computeForecastAccuracy(toTimeSeriesAnalysisRequest(requestId, timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(valueOf(result));
  }

  @Override
  public ResponseEntity<TimeSeriesModel> predict(TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel) {
    long requestId = RequestIdGenerator.nextId();
    LOGGER.info("[{}] Predict request received", requestId);
    TimeSeries result = service.predict(toTimeSeriesAnalysisRequest(requestId, timeSeriesAnalysisRequestModel));
    return ResponseEntity.ok(toTimeSeriesModel(result));
  }
}
