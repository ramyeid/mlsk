package org.mlsk.service.impl.timeseries.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper.buildTimeSeriesRowModel;

public final class TimeSeriesRowMapper {

  private TimeSeriesRowMapper() {
  }

  public static TimeSeriesRow toTimeSeriesRow(TimeSeriesRowModel rowModel) {
    return new TimeSeriesRow(rowModel.getDate(), rowModel.getValue().doubleValue());
  }

  public static TimeSeriesRowModel toTimeSeriesRowModel(TimeSeriesRow timeSeriesRow) {
    return buildTimeSeriesRowModel(timeSeriesRow.getDate(), valueOf(timeSeriesRow.getValue()));
  }
}
