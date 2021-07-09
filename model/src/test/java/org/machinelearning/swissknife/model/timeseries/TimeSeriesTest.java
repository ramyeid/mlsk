package org.machinelearning.swissknife.model.timeseries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeSeriesTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    TimeSeries timeSeries = buildTimeSeriesPassengers();
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedTimeSeries = objectMapper.writeValueAsString(timeSeries);
    TimeSeries deserializedTimeSeries = objectMapper.readValue(serializedTimeSeries, TimeSeries.class);

    assertEquals(deserializedTimeSeries, timeSeries);
  }

  @Test
  public void should_correctly_concatenate_two_time_series() {
    TimeSeries timeSeries = buildTimeSeriesPassengers();
    TimeSeries timeSeries2 = buildTimeSeriesPassengers1();

    TimeSeries actualConcatenatedTimeSeries = TimeSeries.concat(timeSeries, timeSeries2);

    TimeSeries expectedConcatenatedTimeSeries = buildExpectedConcatenatedTimeSeries();
    assertEquals(expectedConcatenatedTimeSeries, actualConcatenatedTimeSeries);
  }

  private static TimeSeries buildExpectedConcatenatedTimeSeries() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow("1960-01", 1.0);
    TimeSeriesRow timeSeriesRow1 = buildTimeSeriesRow("1960-02", 2.0);
    TimeSeriesRow timeSeriesRow2 = buildTimeSeriesRow("1960-03", 3.0);
    TimeSeriesRow timeSeriesRow3 = buildTimeSeriesRow("1960-04", 4.0);
    TimeSeriesRow timeSeriesRow4 = buildTimeSeriesRow("1960-05", 5.0);
    TimeSeriesRow timeSeriesRow5 = buildTimeSeriesRow("1960-06", 6.0);
    TimeSeriesRow timeSeriesRow6 = buildTimeSeriesRow("1961-07", 7.0);
    List<TimeSeriesRow> timeSeriesRows = asList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2, timeSeriesRow3, timeSeriesRow4, timeSeriesRow5, timeSeriesRow6);
    return new TimeSeries(timeSeriesRows, "Date", "Passengers", "%Y-%m");
  }

  static TimeSeries buildTimeSeriesPassengers() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow("1960-01", 1.0);
    TimeSeriesRow timeSeriesRow1 = buildTimeSeriesRow("1960-02", 2.0);
    TimeSeriesRow timeSeriesRow2 = buildTimeSeriesRow("1960-03", 3.0);
    TimeSeriesRow timeSeriesRow3 = buildTimeSeriesRow("1960-04", 4.0);
    TimeSeriesRow timeSeriesRow4 = buildTimeSeriesRow("1960-05", 5.0);
    List<TimeSeriesRow> timeSeriesRows = asList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2, timeSeriesRow3, timeSeriesRow4);
    return new TimeSeries(timeSeriesRows, "Date", "Passengers", "%Y-%m");
  }

  private static TimeSeries buildTimeSeriesPassengers1() {
    TimeSeriesRow timeSeriesRow = buildTimeSeriesRow("1960-06", 6.0);
    TimeSeriesRow timeSeriesRow1 = buildTimeSeriesRow("1961-07", 7.0);
    List<TimeSeriesRow> timeSeriesRows = asList(timeSeriesRow, timeSeriesRow1);
    return new TimeSeries(timeSeriesRows, "Date", "Passengers", "%Y-%m");
  }

  private static TimeSeriesRow buildTimeSeriesRow(String date, Double value) {
    return new TimeSeriesRow(date, value);
  }
}