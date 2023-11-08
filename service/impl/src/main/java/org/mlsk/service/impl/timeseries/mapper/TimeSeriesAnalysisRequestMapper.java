package org.mlsk.service.impl.timeseries.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesMapper.toTimeSeries;

public final class TimeSeriesAnalysisRequestMapper {

  private TimeSeriesAnalysisRequestMapper() {
  }

  public static TimeSeriesAnalysisRequest toTimeSeriesAnalysisRequest(long requestId, TimeSeriesAnalysisRequestModel requestModel) {
    TimeSeries timeSeries = toTimeSeries(requestModel.getTimeSeries());
    return new TimeSeriesAnalysisRequest(requestId, timeSeries, requestModel.getNumberOfValues());
  }

}
