package org.mlsk.service.impl.timeseries.api.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class TimeSeriesMapper {

  private TimeSeriesMapper() {
  }

  public static TimeSeries toTimeSeries(TimeSeriesModel timeSeriesModel) {
    List<TimeSeriesRow> rows = timeSeriesModel.getRows().stream().map(TimeSeriesRowMapper::toTimeSeriesRow).collect(toList());
    String dateColumnName = timeSeriesModel.getDateColumnName();
    String valueColumnName = timeSeriesModel.getValueColumnName();
    String dateFormat = timeSeriesModel.getDateFormat();

    return new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);
  }

  public static TimeSeriesModel toTimeSeriesModel(TimeSeries timeSeries) {
    List<TimeSeriesRowModel> rows = timeSeries.getRows().stream().map(TimeSeriesRowMapper::toTimeSeriesRowModel).collect(toList());
    String dateColumnName = timeSeries.getDateColumnName();
    String valueColumnName = timeSeries.getValueColumnName();
    String dateFormat = timeSeries.getDateFormat();

    return new TimeSeriesModel(rows, dateColumnName, valueColumnName, dateFormat);
  }
}
