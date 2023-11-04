package org.mlsk.ui.timeseries.csv;

import org.junit.jupiter.api.Test;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.ui.exception.CsvParsingException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.timeseries.csv.CsvToTimeSeries.toTimeSeries;

public class CsvToTimeSeriesTest {

  @Test
  public void should_be_able_to_build_time_series_from_csv() throws CsvParsingException {
    ClassLoader classLoader = getClass().getClassLoader();
    String csvLocation = Objects.requireNonNull(classLoader.getResource("time_series_passengers.csv")).getFile();

    TimeSeriesModel actualTimeSeries = toTimeSeries(csvLocation, "Date", "Passengers", "%Y-%m");

    TimeSeriesModel expectedTimeSeries = buildTimeSeriesPassengers();
    assertEquals(expectedTimeSeries, actualTimeSeries);
  }

  @Test
  public void should_throw_exception_if_header_does_not_contain_value_column_name() {
    ClassLoader classLoader = getClass().getClassLoader();
    String csvLocation = Objects.requireNonNull(classLoader.getResource("time_series_passengers.csv")).getFile();

    try {
      toTimeSeries(csvLocation, "Date", "PASSENGER", "%Y-%m");
      fail("Date Column Name does not exit");

    } catch (Exception exception) {
      assertOnCsvParsingException(exception, "Header does not contain 'PASSENGER' column");
    }
  }

  @Test
  public void should_throw_exception_if_header_does_not_contain_date_column_name() {
    ClassLoader classLoader = getClass().getClassLoader();
    String csvLocation = Objects.requireNonNull(classLoader.getResource("time_series_passengers.csv")).getFile();

    try {
      toTimeSeries(csvLocation, "DAT", "Passengers", "%Y-%m");
      fail("Date Column Name does not exit");

    } catch (Exception exception) {
      assertOnCsvParsingException(exception, "Header does not contain 'DAT' column");
    }
  }

  private static TimeSeriesModel buildTimeSeriesPassengers() {
    TimeSeriesRowModel timeSeriesRow = buildTimeSeriesRow("1960-01", 1.0);
    TimeSeriesRowModel timeSeriesRow1 = buildTimeSeriesRow("1960-02", 2.0);
    TimeSeriesRowModel timeSeriesRow2 = buildTimeSeriesRow("1960-03", 3.0);
    TimeSeriesRowModel timeSeriesRow3 = buildTimeSeriesRow("1960-04", 4.0);
    TimeSeriesRowModel timeSeriesRow4 = buildTimeSeriesRow("1960-05", 5.0);
    List<TimeSeriesRowModel> timeSeriesRows = newArrayList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2, timeSeriesRow3, timeSeriesRow4);
    return new TimeSeriesModel()
        .rows(timeSeriesRows)
        .dateColumnName("Date")
        .valueColumnName("Passengers")
        .dateFormat("%Y-%m");
  }

  private static TimeSeriesRowModel buildTimeSeriesRow(String date, Double value) {
    return new TimeSeriesRowModel().date(date).value(BigDecimal.valueOf(value));
  }

  private static void assertOnCsvParsingException(Exception exception, String exceptionMessage) {
    assertInstanceOf(CsvParsingException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}