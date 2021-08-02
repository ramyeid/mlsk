package org.mlsk.service.impl.mapper.timeseries;

import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesMapper.toTimeSeries;

public final class TimeSeriesAnalysisRequestMapper {

  private TimeSeriesAnalysisRequestMapper() {
  }

  public static TimeSeriesAnalysisRequest toTimeSeriesAnalysisRequest(TimeSeriesAnalysisRequestModel requestModel) {
    TimeSeries timeSeries = toTimeSeries(requestModel.getTimeSeries());
    return new TimeSeriesAnalysisRequest(timeSeries, requestModel.getNumberOfValues());
  }

}
