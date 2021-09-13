package org.mlsk.ui.timeseries.request;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.mlsk.ui.timeseries.csv.CsvToTimeSeries.toTimeSeries;

public class TimeSeriesAnalysisRequestBuilder {

  public TimeSeriesAnalysisRequest buildRequest(String dateColumnName, String valueColumnName, String dateFormat, String csvLocation, String numberOfValuesStr) {
    try {
      int numberOfValues = parseInt(numberOfValuesStr);

      TimeSeries timeSeries = toTimeSeries(csvLocation, dateColumnName, valueColumnName, dateFormat);

      return new TimeSeriesAnalysisRequest(timeSeries, numberOfValues);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisRequestBuilderException(exception);
    }
  }
}
