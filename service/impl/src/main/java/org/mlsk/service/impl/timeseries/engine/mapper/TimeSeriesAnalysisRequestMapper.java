package org.mlsk.service.impl.timeseries.engine.mapper;

import org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import static org.mlsk.service.impl.timeseries.engine.mapper.TimeSeriesMapper.toTimeSeriesModel;

public final class TimeSeriesAnalysisRequestMapper {

  private TimeSeriesAnalysisRequestMapper() {
  }

  public static TimeSeriesAnalysisRequestModel toTimeSeriesAnalysisRequestModel(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    return new TimeSeriesAnalysisRequestModel(timeSeriesAnalysisRequest.getRequestId(), toTimeSeriesModel(timeSeriesAnalysisRequest.getTimeSeries()), timeSeriesAnalysisRequest.getNumberOfValues());
  }
}
