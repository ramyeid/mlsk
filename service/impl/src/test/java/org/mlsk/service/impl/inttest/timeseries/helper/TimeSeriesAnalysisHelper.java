package org.mlsk.service.impl.inttest.timeseries.helper;

import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper;
import org.mlsk.service.impl.timeseries.service.exception.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper.buildTimeSeriesModel;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper.buildTimeSeriesRowModel;

public final class TimeSeriesAnalysisHelper {

  private TimeSeriesAnalysisHelper() {
  }

  public static TimeSeries buildTimeSeriesResult() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 1.);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 2.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  public static TimeSeriesModel buildTimeSeriesModelResult() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date1", valueOf(1.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date2", valueOf(2.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy-MM");
  }

  public static TimeSeries buildTimeSeriesResult2() {
    TimeSeriesRow row1 = new TimeSeriesRow("date3", 3.);
    TimeSeriesRow row2 = new TimeSeriesRow("date4", 4.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    return new TimeSeries(rows, "date", "value", "yyyy");
  }

  public static TimeSeriesModel buildTimeSeriesModelResult2() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date3", valueOf(3.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date4", valueOf(4.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return buildTimeSeriesModel(rows, "date", "value", "yyyy");
  }

  public static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);
    TimeSeriesRow row3 = new TimeSeriesRow("date1", 33.);
    TimeSeriesRow row4 = new TimeSeriesRow("date2", 44.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3, row4);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  public static TimeSeriesAnalysisRequestModel buildTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date-1", valueOf(11.));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date0", valueOf(22.));
    TimeSeriesRowModel row3 = buildTimeSeriesRowModel("date1", valueOf(33.));
    TimeSeriesRowModel row4 = buildTimeSeriesRowModel("date2", valueOf(44.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3, row4);
    TimeSeriesModel timeSeries = buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return TimeSeriesModelHelper.buildTimeSeriesAnalysisRequestModel(timeSeries, 2);
  }

  public static TimeSeriesAnalysisRequest buildTimeSeriesAnalysisExpectedRequestForecastVsActual() {
    TimeSeriesRow row1 = new TimeSeriesRow("date-1", 11.);
    TimeSeriesRow row2 = new TimeSeriesRow("date0", 22.);

    List<TimeSeriesRow> rows = newArrayList(row1, row2);
    TimeSeries timeSeries = new TimeSeries(rows, "dateColumnName", "valueColumnName", "yyyy-MM");

    return new TimeSeriesAnalysisRequest(timeSeries, 2);
  }

  public static void assertOnTimeSeriesAnalysisServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}
