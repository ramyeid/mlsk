package org.mlsk.service.impl.mapper.timeseries;

import org.junit.jupiter.api.Test;
import org.mlsk.api.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.timeseries.model.TimeSeriesModel;
import org.mlsk.api.timeseries.model.TimeSeriesRowModel;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.mapper.timeseries.TimeSeriesAnalysisRequestMapper.toTimeSeriesAnalysisRequest;

public class TimeSeriesAnalysisRequestMapperTest {

  @Test
  public void should_correctly_map_to_time_series_analysis_request() {
    TimeSeriesAnalysisRequestModel requestModel = buildRequestModel();

    TimeSeriesAnalysisRequest actualRequest = toTimeSeriesAnalysisRequest(requestModel);

    assertEquals(buildExpectedRequest(), actualRequest);
  }

  private static TimeSeriesAnalysisRequestModel buildRequestModel() {
    TimeSeriesRowModel row1 = new TimeSeriesRowModel().date("date1").value(valueOf(123.1));
    TimeSeriesRowModel row2 = new TimeSeriesRowModel().date("date2").value(valueOf(124.1));
    TimeSeriesRowModel row3 = new TimeSeriesRowModel().date("date3").value(valueOf(125.1));

    List<TimeSeriesRowModel> rows = newArrayList(row1, row2, row3);
    String dateColumnName = "dateColumnName";
    String valueColumnName = "valueColumnName";
    String dateFormat = "dateFormat";
    TimeSeriesModel timeSeriesModel = new TimeSeriesModel().rows(rows).dateColumnName(dateColumnName).valueColumnName(valueColumnName).dateFormat(dateFormat);

    int numberOfValues = 123;
    return new TimeSeriesAnalysisRequestModel().timeSeries(timeSeriesModel).numberOfValues(numberOfValues);
  }

  private static TimeSeriesAnalysisRequest buildExpectedRequest() {
    TimeSeriesRow row1 = new TimeSeriesRow("date1", 123.1);
    TimeSeriesRow row2 = new TimeSeriesRow("date2", 124.1);
    TimeSeriesRow row3 = new TimeSeriesRow("date3", 125.1);

    List<TimeSeriesRow> rows = newArrayList(row1, row2, row3);
    String dateColumnName = "dateColumnName";
    String valueColumnName = "valueColumnName";
    String dateFormat = "dateFormat";
    TimeSeries timeSeries = new TimeSeries(rows, dateColumnName, valueColumnName, dateFormat);

    int numberOfValues = 123;
    return new TimeSeriesAnalysisRequest(timeSeries, numberOfValues);
  }

}