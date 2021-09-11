package org.mlsk.ui.timeseries.mapper;

import org.jfree.data.time.Millisecond;
import org.junit.jupiter.api.Test;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.timeseries.mapper.TimeSeriesMapper.toTimeSeries;

public class TimeSeriesMapperTest {

  private static final String TITLE = "title";
  public static final String DATE_FORMAT = "yyyy-MM";

  @Test
  public void should_map_time_series() throws ParseException {
    TimeSeries timeSeries = buildTimeSeries(DATE_FORMAT);

    org.jfree.data.time.TimeSeries actual = toTimeSeries(timeSeries, TITLE);

    assertEquals(buildExpectedTimeSeries(), actual);
  }

  @Test
  public void should_throw_exception_on_error() {
    TimeSeries timeSeries = buildTimeSeries("yyyy-MM-dd");

    try {
      toTimeSeries(timeSeries, TITLE);
      fail("should fail while parsing date");

    } catch (Exception exception) {
      assertInstanceOf(MappingTimeSeriesException.class, exception);
      assertInstanceOf(ParseException.class, exception.getCause());
      assertEquals("Error while mapping time series: Unparseable date: \"1990-01\"", exception.getMessage());
    }
  }

  private static TimeSeries buildTimeSeries(String dateFormat) {
    TimeSeriesRow row1 = new TimeSeriesRow("1990-01", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("1990-02", 2.);
    TimeSeriesRow row3 = new TimeSeriesRow("1990-03", 3.);
    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);

    return new TimeSeries(rows, "date", "value", dateFormat);
  }

  private static org.jfree.data.time.TimeSeries buildExpectedTimeSeries() throws ParseException {
    org.jfree.data.time.TimeSeries timeSeries = new org.jfree.data.time.TimeSeries(TITLE);

    SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    timeSeries.add(new Millisecond(dateFormatter.parse("1990-01")), 1.);
    timeSeries.add(new Millisecond(dateFormatter.parse("1990-02")), 2.);
    timeSeries.add(new Millisecond(dateFormatter.parse("1990-03")), 3.);

    return timeSeries;
  }
}