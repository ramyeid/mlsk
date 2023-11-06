package org.mlsk.service.impl.timeseries.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.timeseries.engine.mapper.TimeSeriesRowMapper.fromEngineModel;
import static org.mlsk.service.impl.timeseries.engine.mapper.TimeSeriesRowMapper.toEngineModel;

public class TimeSeriesRowMapperTest {

  @Test
  public void should_correctly_map_to_time_series_row() {
    TimeSeriesRowModel timeSeriesRowModel = buildTimeSeriesRowModel();

    TimeSeriesRow actualTimeSeriesRow = fromEngineModel(timeSeriesRowModel);

    assertEquals(buildExpectedTimeSeriesRow(), actualTimeSeriesRow);
  }

  @Test
  public void should_correctly_map_to_time_series_row_model() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow();

    TimeSeriesRowModel actualTimeSeriesRowModel = toEngineModel(timeSeriesRow);

    assertEquals(buildExpectedTimeSeriesRowModel(), actualTimeSeriesRowModel);
  }

  private static TimeSeriesRow buildTimeSeriesRow() {
    return new TimeSeriesRow("date1", 123.1);
  }

  private static TimeSeriesRowModel buildExpectedTimeSeriesRowModel() {
    return new TimeSeriesRowModel("date1", valueOf(123.1));
  }

  private static TimeSeriesRowModel buildTimeSeriesRowModel() {
    return new TimeSeriesRowModel("1", valueOf(1));
  }

  private static TimeSeriesRow buildExpectedTimeSeriesRow() {
    return new TimeSeriesRow("1", 1.);
  }

}