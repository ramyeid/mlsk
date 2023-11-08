package org.mlsk.ui.timeseries.request;

import org.mlsk.api.service.timeseries.model.TimeSeriesAnalysisRequestModel;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;

import static java.lang.Integer.parseInt;
import static org.mlsk.ui.timeseries.csv.CsvToTimeSeries.toTimeSeries;

public class TimeSeriesAnalysisRequestBuilder {

  public TimeSeriesAnalysisRequestModel buildRequest(String dateColumnName, String valueColumnName, String dateFormat, String csvLocation, String numberOfValuesStr) {
    try {
      int numberOfValues = parseInt(numberOfValuesStr);

      TimeSeriesModel timeSeries = toTimeSeries(csvLocation, dateColumnName, valueColumnName, dateFormat);

      return new TimeSeriesAnalysisRequestModel(timeSeries, numberOfValues);
    } catch (Exception exception) {
      throw new TimeSeriesAnalysisRequestBuilderException(exception);
    }
  }
}
