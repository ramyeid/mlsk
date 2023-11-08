package org.mlsk.service.impl.timeseries.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesRowModel;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.timeseries.mapper.TimeSeriesModelHelper.*;

public class TimeSeriesModelHelperTest {

  @Test
  public void should_correctly_build_time_series_row_model() {
    String date = "date";
    BigDecimal value = BigDecimal.valueOf(123.987);

    TimeSeriesRowModel actualTimeSeriesRowModel = buildTimeSeriesRowModel(date, value);

    TimeSeriesRowModel expectedTimeSeriesRowModel = new TimeSeriesRowModel(date, value);
    assertEquals(expectedTimeSeriesRowModel, actualTimeSeriesRowModel);
  }

  @Test
  public void should_correctly_build_time_series_model() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date1", BigDecimal.valueOf(1.1));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date2", BigDecimal.valueOf(2.2));
    TimeSeriesRowModel row3 = buildTimeSeriesRowModel("date3", BigDecimal.valueOf(3.3));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);
    String dateColumnName = "dateColumnName";
    String valueColumnName = "valueColumnName";
    String dateFormat = "dateFormat";

    TimeSeriesModel actualTimeSeriesModel = buildTimeSeriesModel(rows, dateColumnName, valueColumnName, dateFormat);

    TimeSeriesModel expectedTimeSeriesModel = new TimeSeriesModel(rows, dateColumnName, valueColumnName, dateFormat);
    assertEquals(expectedTimeSeriesModel, actualTimeSeriesModel);
  }

  @Test
  public void should_correctly_build_time_series_analysis_request_model() {
    TimeSeriesRowModel row1 = buildTimeSeriesRowModel("date1", BigDecimal.valueOf(1.1));
    TimeSeriesRowModel row2 = buildTimeSeriesRowModel("date2", BigDecimal.valueOf(2.2));
    TimeSeriesRowModel row3 = buildTimeSeriesRowModel("date3", BigDecimal.valueOf(3.3));
    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);
    TimeSeriesModel timeSeriesModel = buildTimeSeriesModel(rows, "dateColumnName", "valueColumnName", "dateFormat");
    int numberOfValues = 123;

    TimeSeriesAnalysisRequestModel actualRequestModel = buildTimeSeriesAnalysisRequestModel(timeSeriesModel, numberOfValues);

    TimeSeriesAnalysisRequestModel expectedRequestModel = new TimeSeriesAnalysisRequestModel(timeSeriesModel, numberOfValues);
    assertEquals(expectedRequestModel, actualRequestModel);
  }
}