package org.mlsk.service.impl.mapper.timeseries;

import org.junit.jupiter.api.Test;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesRowMapper.toTimeSeriesRow;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesRowMapper.toTimeSeriesRowModel;

public class TimeSeriesRowMapperTest {

  @Test
  public void should_correctly_map_to_time_series_row() {
    TimeSeriesRowModel timeSeriesRowModel = buildTimeSeriesRowModel();

    TimeSeriesRow actualTimeSeriesRow = toTimeSeriesRow(timeSeriesRowModel);

    assertEquals(buildExpectedTimeSeriesRow(), actualTimeSeriesRow);
  }

  @Test
  public void should_correctly_map_to_time_series_row_model() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow();

    TimeSeriesRowModel actualTimeSeriesRowModel = toTimeSeriesRowModel(timeSeriesRow);

    assertEquals(buildExpectedTimeSeriesRowModel(), actualTimeSeriesRowModel);
  }

  private static TimeSeriesRow buildTimeSeriesRow() {
    return new TimeSeriesRow("date1", 123.1);
  }

  private static TimeSeriesRowModel buildExpectedTimeSeriesRowModel() {
    return new TimeSeriesRowModel().date("date1").value(valueOf(123.1));
  }

  private static TimeSeriesRowModel buildTimeSeriesRowModel() {
    return new TimeSeriesRowModel().date("1").value(valueOf(1));
  }

  private static TimeSeriesRow buildExpectedTimeSeriesRow() {
    return new TimeSeriesRow("1", 1.);
  }
}