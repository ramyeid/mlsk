package org.mlsk.service.impl.mapper.timeseries;

import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import java.math.BigDecimal;
import java.util.List;

public final class TimeSeriesModelHelper {

  private TimeSeriesModelHelper() {
  }

  public static TimeSeriesRowModel buildTimeSeriesRowModel(String date, BigDecimal value) {
    TimeSeriesRowModel timeSeriesRowModel = new TimeSeriesRowModel();
    timeSeriesRowModel.setDate(date);
    timeSeriesRowModel.setValue(value);
    return timeSeriesRowModel;
  }

  public static TimeSeriesModel buildTimeSeriesModel(List<TimeSeriesRowModel> rows, String dateColumnName, String valueColumnName, String dateFormat) {
    TimeSeriesModel timeSeriesModel = new TimeSeriesModel();
    timeSeriesModel.setRows(rows);
    timeSeriesModel.setDateColumnName(dateColumnName);
    timeSeriesModel.setValueColumnName(valueColumnName);
    timeSeriesModel.setDateFormat(dateFormat);
    return timeSeriesModel;
  }

  public static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel(TimeSeriesModel timeSeries, int numberOfValues) {
    TimeSeriesAnalysisRequestModel timeSeriesAnalysisRequestModel = new TimeSeriesAnalysisRequestModel();
    timeSeriesAnalysisRequestModel.setTimeSeries(timeSeries);
    timeSeriesAnalysisRequestModel.setNumberOfValues(numberOfValues);
    return timeSeriesAnalysisRequestModel;
  }

}
