package org.mlsk.service.impl.timeseries.api.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

public final class TimeSeriesAnalysisRequestMapper {

  private TimeSeriesAnalysisRequestMapper() {
  }

  public static TimeSeriesAnalysisRequest fromServiceModel(long requestId, TimeSeriesAnalysisRequestModel requestModel) {
    TimeSeries timeSeries = TimeSeriesMapper.fromServiceModel(requestModel.getTimeSeries());
    return new TimeSeriesAnalysisRequest(requestId, timeSeries, requestModel.getNumberOfValues());
  }

}
