package org.mlsk.ui.timeseries.mapper;

import org.jfree.data.time.Millisecond;
import org.junit.jupiter.api.Test;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.ui.timeseries.mapper.TimeSeriesMapper.toTimeSeries;

public class TimeSeriesMapperTest {

  private static final String TITLE = "title";
  private static final String DATE_FORMAT = "yyyy-MM";

  @Test
  public void should_map_time_series() throws ParseException {
    TimeSeriesModel timeSeries = buildTimeSeries(DATE_FORMAT);

    org.jfree.data.time.TimeSeries actual = toTimeSeries(timeSeries, TITLE);

    assertEquals(buildExpectedTimeSeries(), actual);
  }

  @Test
  public void should_throw_exception_on_error() {
    TimeSeriesModel timeSeries = buildTimeSeries("yyyy-MM-dd");

    try {
      toTimeSeries(timeSeries, TITLE);
      fail("should fail while parsing date");

    } catch (Exception exception) {
      assertInstanceOf(MappingTimeSeriesException.class, exception);
      assertInstanceOf(ParseException.class, exception.getCause());
      assertEquals("java.text.ParseException: Unparseable date: \"1990-01\"", exception.getMessage());
    }
  }

  private static TimeSeriesModel buildTimeSeries(String dateFormat) {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel().date("1990-01").value(valueOf(1.d));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel().date("1990-02").value(valueOf(2.d));
    TimeSeriesRowModel row3 = new TimeSeriesRowModel().date("1990-03").value(valueOf(3.d));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);

    return new TimeSeriesModel()
        .rows(rows)
        .dateColumnName("date")
        .valueColumnName("value")
        .dateFormat(dateFormat);
  }

  private static org.jfree.data.time.TimeSeries buildExpectedTimeSeries() throws ParseException {
    org.jfree.data.time.TimeSeries timeSeries = new org.jfree.data.time.TimeSeries(TITLE);

    SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    timeSeries.add(new Millisecond(dateFormatter.parse("1990-01")), valueOf(1.d));
    timeSeries.add(new Millisecond(dateFormatter.parse("1990-02")), valueOf(2.d));
    timeSeries.add(new Millisecond(dateFormatter.parse("1990-03")), valueOf(3.d));

    return timeSeries;
  }
}