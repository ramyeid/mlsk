package org.mlsk.service.impl.timeseries.engine.mapper;

import org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import static java.math.BigDecimal.valueOf;

public final class TimeSeriesRowMapper {

  private TimeSeriesRowMapper() {
  }

  public static TimeSeriesRow fromEngineModel(TimeSeriesRowModel timeSeriesRowModel) {
    return new TimeSeriesRow(timeSeriesRowModel.getDate(), timeSeriesRowModel.getValue().doubleValue());
  }

  public static TimeSeriesRowModel toEngineModel(TimeSeriesRow timeSeriesRow) {
    return new TimeSeriesRowModel(timeSeriesRow.getDate(), valueOf(timeSeriesRow.getValue()));
  }
}
