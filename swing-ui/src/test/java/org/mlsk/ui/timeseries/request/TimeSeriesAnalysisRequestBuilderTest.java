package org.mlsk.ui.timeseries.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.ui.exception.CsvParsingException;
import org.mlsk.ui.timeseries.csv.CsvToTimeSeries;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class TimeSeriesAnalysisRequestBuilderTest {

  private TimeSeriesAnalysisRequestBuilder requestBuilder;

  @BeforeEach
  public void setUp() {
    this.requestBuilder = new TimeSeriesAnalysisRequestBuilder();
  }

  @Test
  public void should_build_request() {
    try (MockedStatic<CsvToTimeSeries> mockedStatic = Mockito.mockStatic(CsvToTimeSeries.class)) {
      String dateColumnValue = "date";
      String valueColumnName = "value";
      String dateFormat = "dateFormat";
      String csvLocation = "csvLocation";
      onParseCsvReturn(mockedStatic, buildTimeSeries());

      TimeSeriesAnalysisRequestModel actual = requestBuilder.buildRequest(dateColumnValue, valueColumnName, dateFormat, csvLocation, "4");

      TimeSeriesAnalysisRequestModel expected = new TimeSeriesAnalysisRequestModel(buildTimeSeries(), 4);
      assertEquals(expected, actual);
    }
  }

  @Test
  public void should_throw_exception_if_number_of_values_is_not_int() {
    String dateColumnValue = "date";
    String valueColumnName = "value";
    String dateFormat = "dateFormat";
    String csvLocation = "csvLocation";

    try {
      requestBuilder.buildRequest(dateColumnValue, valueColumnName, dateFormat, csvLocation, "NotAnInt");
      fail("should not fail since number of values not int");

    } catch (Exception exception) {
      assertInstanceOf(TimeSeriesAnalysisRequestBuilderException.class, exception);
      assertInstanceOf(NumberFormatException.class, exception.getCause());
      assertEquals("java.lang.NumberFormatException: For input string: \"NotAnInt\"", exception.getMessage());
    }
  }

  @Test
  public void should_throw_exception_if_parsing_csv_fails() {
    try (MockedStatic<CsvToTimeSeries> mockedStatic = Mockito.mockStatic(CsvToTimeSeries.class)) {
      String dateColumnValue = "date";
      String valueColumnName = "value";
      String dateFormat = "dateFormat";
      String csvLocation = "csvLocation";
      doThrowExceptionOnParseCsv(mockedStatic, new CsvParsingException("exceptionMessage"));

      try {
        requestBuilder.buildRequest(dateColumnValue, valueColumnName, dateFormat, csvLocation, "4");
        fail("should fail since parsing csv threw exception");

      } catch (Exception exception) {
        assertInstanceOf(TimeSeriesAnalysisRequestBuilderException.class, exception);
        assertInstanceOf(CsvParsingException.class, exception.getCause());
        assertEquals("org.mlsk.ui.exception.CsvParsingException: exceptionMessage", exception.getMessage());
      }
    }
  }

  private static void onParseCsvReturn(MockedStatic<CsvToTimeSeries> mockedStatic, TimeSeriesModel timeSeries) {
    mockedStatic.when(() -> CsvToTimeSeries.toTimeSeries(anyString(), anyString(), anyString(), anyString())).thenReturn(timeSeries);
  }

  private static void doThrowExceptionOnParseCsv(MockedStatic<CsvToTimeSeries> mockedStatic, Exception exception) {
    mockedStatic.when(() -> CsvToTimeSeries.toTimeSeries(anyString(), anyString(), anyString(), anyString())).thenThrow(exception);
  }

  private static TimeSeriesModel buildTimeSeries() {
    TimeSeriesRowModel row = new TimeSeriesRowModel("1990", BigDecimal.valueOf(1.));
    List<TimeSeriesRowModel> rows = newArrayList(row);

    return new TimeSeriesModel(rows, "Date", "Value", "Yyyy");
  }
}