package org.mlsk.service.impl.timeseries.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;

import java.math.BigDecimal;
import java.util.List;

public final class TimeSeriesModelHelper {

  private TimeSeriesModelHelper() {
  }

  public static TimeSeriesRowModel buildTimeSeriesRowModel(String date, BigDecimal value) {
    return new TimeSeriesRowModel(date, value);
  }

  public static TimeSeriesModel buildTimeSeriesModel(List<TimeSeriesRowModel> rows, String dateColumnName, String valueColumnName, String dateFormat) {
    return new TimeSeriesModel(rows, dateColumnName, valueColumnName, dateFormat);
  }

  public static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel(TimeSeriesModel timeSeries, int numberOfValues) {
    return new TimeSeriesAnalysisRequestModel(timeSeries, numberOfValues);
  }

}
