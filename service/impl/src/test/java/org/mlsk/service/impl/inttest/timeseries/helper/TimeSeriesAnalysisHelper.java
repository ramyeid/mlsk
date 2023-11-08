package org.mlsk.service.impl.inttest.timeseries.helper;

import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.impl.timeseries.service.exception.TimeSeriesAnalysisServiceException;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class TimeSeriesAnalysisHelper {

  private TimeSeriesAnalysisHelper() {
  }

  public static org.mlsk.api.engine.timeseries.model.TimeSeriesModel buildEngineTimeSeriesResultModel() {
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row1 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date1", valueOf(1.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row2 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date2", valueOf(2.));

    List<org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return new org.mlsk.api.engine.timeseries.model.TimeSeriesModel(
        rows,
        "dateColumnName",
        "valueColumnName",
        "yyyy-MM"
    );
  }

  public static TimeSeriesModel buildServiceTimeSeriesModelResultModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel("date1", valueOf(1.));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel("date2", valueOf(2.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return new TimeSeriesModel(
        rows,
        "dateColumnName",
        "valueColumnName",
        "yyyy-MM"
    );
  }

  public static org.mlsk.api.engine.timeseries.model.TimeSeriesModel buildEngineTimeSeriesResult2Model() {
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row1 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date3", valueOf(3.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row2 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date4", valueOf(4.));

    List<org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return new org.mlsk.api.engine.timeseries.model.TimeSeriesModel(
        rows,
        "date",
        "value",
        "yyyy"
    );
  }

  public static TimeSeriesModel buildServiceTimeSeriesModelResult2Model() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel("date3", valueOf(3.));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel("date4", valueOf(4.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2);
    return new TimeSeriesModel(
        rows,
        "date",
        "value",
        "yyyy"
    );
  }

  public static org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel buildEngineTimeSeriesAnalysisRequestModel(long requestId) {
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row1 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date-1", valueOf(11.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row2 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date0", valueOf(22.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row3 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date1", valueOf(33.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row4 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date2", valueOf(44.));

    List<org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel> rows = newArrayList(row1, row2, row3, row4);
    org.mlsk.api.engine.timeseries.model.TimeSeriesModel timeSeries = new org.mlsk.api.engine.timeseries.model.TimeSeriesModel(
        rows,
        "dateColumnName",
        "valueColumnName",
        "yyyy-MM"
    );

    return new org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel(requestId, timeSeries, 2);
  }

  public static TimeSeriesAnalysisRequestModel buildServiceTimeSeriesAnalysisRequestModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel("date-1", valueOf(11.));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel("date0", valueOf(22.));
    TimeSeriesRowModel row3 = new TimeSeriesRowModel("date1", valueOf(33.));
    TimeSeriesRowModel row4 = new TimeSeriesRowModel("date2", valueOf(44.));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3, row4);
    TimeSeriesModel timeSeries = new TimeSeriesModel(
        rows,
        "dateColumnName",
        "valueColumnName",
        "yyyy-MM"
    );

    return new TimeSeriesAnalysisRequestModel(timeSeries, 2);
  }

  public static org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel buildEngineTimeSeriesAnalysisExpectedRequestForecastVsActualModel(long requestId) {
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row1 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date-1", valueOf(11.));
    org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel row2 = new org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel("date0", valueOf(22.));

    List<org.mlsk.api.engine.timeseries.model.TimeSeriesRowModel> rows = newArrayList(row1, row2);
    org.mlsk.api.engine.timeseries.model.TimeSeriesModel timeSeries = new org.mlsk.api.engine.timeseries.model.TimeSeriesModel(
        rows,
        "dateColumnName",
        "valueColumnName",
        "yyyy-MM"
    );

    return new org.mlsk.api.engine.timeseries.model.TimeSeriesAnalysisRequestModel(requestId, timeSeries, 2);
  }

  public static void assertOnTimeSeriesAnalysisServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(TimeSeriesAnalysisServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}