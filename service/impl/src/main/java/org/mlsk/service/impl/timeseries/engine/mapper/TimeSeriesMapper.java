package org.mlsk.service.impl.timeseries.engine.mapper;

import org.mlsk.api.engine.timeseries.model.TimeSeriesModel;
import org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class TimeSeriesMapper {

  private TimeSeriesMapper() {
  }

  public static TimeSeries fromEngineModel(TimeSeriesModel timeSeriesModel) {
    List<TimeSeriesRow> rows = timeSeriesModel.getRows().stream().map(TimeSeriesRowMapper::fromEngineModel).collect(toList());
    String dateColumnName = timeSeriesModel.getDateColumnName();
    String valueColumnName = timeSeriesModel.getValueColumnName();
    String dateFormat = timeSeriesModel.getDateFormat();

    return new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);
  }

  public static TimeSeriesModel toEngineModel(TimeSeries timeSeries) {
    List<TimeSeriesRowModel> rows = timeSeries.getRows().stream().map(TimeSeriesRowMapper::toEngineModel).collect(toList());
    String dateColumnName = timeSeries.getDateColumnName();
    String valueColumnName = timeSeries.getValueColumnName();
    String dateFormat = timeSeries.getDateFormat();

    return new TimeSeriesModel(rows, dateColumnName, valueColumnName, dateFormat);
  }
}
