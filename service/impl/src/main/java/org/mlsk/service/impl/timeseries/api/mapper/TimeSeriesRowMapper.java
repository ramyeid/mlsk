package org.mlsk.service.impl.timeseries.api.mapper;

import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import static java.math.BigDecimal.valueOf;

public final class TimeSeriesRowMapper {

  private TimeSeriesRowMapper() {
  }

  public static TimeSeriesRow fromServiceModel(TimeSeriesRowModel rowModel) {
    return new TimeSeriesRow(rowModel.getDate(), rowModel.getValue().doubleValue());
  }

  public static TimeSeriesRowModel toServiceModel(TimeSeriesRow timeSeriesRow) {
    return new TimeSeriesRowModel(timeSeriesRow.getDate(), valueOf(timeSeriesRow.getValue()));
  }
}
