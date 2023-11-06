package org.mlsk.service.impl.timeseries.engine.mapper;

import org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

public final class TimeSeriesAnalysisRequestMapper {

  private TimeSeriesAnalysisRequestMapper() {
  }

  public static TimeSeriesAnalysisRequestModel toEngineModel(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return new TimeSeriesAnalysisRequestModel(timeSeriesAnalysisRequest.getRequestId(), TimeSeriesMapper.toEngineModel(timeSeriesAnalysisRequest.getTimeSeries()), timeSeriesAnalysisRequest.getNumberOfValues());
  }
}
