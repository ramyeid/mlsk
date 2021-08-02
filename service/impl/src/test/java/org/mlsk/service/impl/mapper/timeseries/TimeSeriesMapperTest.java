package org.mlsk.service.impl.mapper.timeseries;

import org.junit.jupiter.api.Test;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesMapper.toTimeSeries;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesMapper.toTimeSeriesModel;

public class TimeSeriesMapperTest {

  @Test
  public void should_correctly_map_to_time_series() {
    TimeSeriesModel timeSeriesModel = buildTimeSeriesModel();

    TimeSeries actualTimeSeries = toTimeSeries(timeSeriesModel);

    assertEquals(buildExpectedTimeSeries(), actualTimeSeries);
  }

  @Test
  public void should_correctly_map_to_time_series_model() {
    TimeSeries timeSeries = buildTimeSeries();

    TimeSeriesModel actualTimeSeriesModel = toTimeSeriesModel(timeSeries);

    assertEquals(buildExpectedTimeSeriesModel(), actualTimeSeriesModel);
  }

  private static TimeSeries buildTimeSeries() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.1);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 941.2);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    String dateColumnName = "ActionDate";
    String valueColumnName = "NumberOfEmployees";
    String dateFormat = "yyyy-MM-dd";
    return new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);
  }

  private static TimeSeriesModel buildExpectedTimeSeriesModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel().date("date1").value(valueOf(123.1));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel().date("date2").value(valueOf(941.2));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    String dateColumnName = "ActionDate";
    String valueColumnName = "NumberOfEmployees";
    String dateFormat = "yyyy-MM-dd";
    return new TimeSeriesModel().rows(rows).dateColumnName(dateColumnName).valueColumnName(valueColumnName).dateFormat(dateFormat);
  }

  private static TimeSeriesModel buildTimeSeriesModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel().date("1").value(valueOf(1));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel().date("2").value(valueOf(2));
    TimeSeriesRowModel row3 = new TimeSeriesRowModel().date("3").value(valueOf(3));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);
    String dateColumnName = "Date";
    String valueColumnName = "Passengers";
    String dateFormat = "yyyy-MM";
    return new TimeSeriesModel().rows(rows).dateColumnName(dateColumnName).valueColumnName(valueColumnName).dateFormat(dateFormat);
  }

  private static TimeSeries buildExpectedTimeSeries() {
    TimeSeriesRow row1 = new TimeSeriesRow("1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("2", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("3", 3.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);
    String dateColumnName = "Date";
    String valueColumnName = "Passengers";
    String dateFormat = "yyyy-MM";
    return new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);
  }
}