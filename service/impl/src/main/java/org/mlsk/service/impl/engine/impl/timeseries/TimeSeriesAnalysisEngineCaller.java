package org.mlsk.service.impl.engine.impl.timeseries;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.service.TimeSeriesAnalysis;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import java.util.List;

public class TimeSeriesAnalysisEngineCaller implements TimeSeriesAnalysis {

  private final ServiceInformation serviceInformation;
  private final EngineClientFactory engineClientFactory;

  public TimeSeriesAnalysisEngineCaller(ServiceInformation serviceInformation) {
    this(serviceInformation, new EngineClientFactory());
  }

  @VisibleForTesting
  public TimeSeriesAnalysisEngineCaller(ServiceInformation serviceInformation, EngineClientFactory engineClientFactory) {
    this.serviceInformation = serviceInformation;
    this.engineClientFactory = engineClientFactory;
  }

  @Override
  public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.forecast(timeSeriesAnalysisRequest);
  }

  @Override
  public TimeSeries forecastVsActual(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    int numberOfValues = timeSeriesAnalysisRequest.getNumberOfValues();
    TimeSeries timeSeries = timeSeriesAnalysisRequest.getTimeSeries();
    List<TimeSeriesRow> rows = timeSeries.getRows();

    List<TimeSeriesRow> newRows = rows.subList(0, rows.size() - numberOfValues);
    TimeSeries newTimeSeries = new TimeSeries(newRows, timeSeries.getDateColumnName(), timeSeries.getValueColumnName(), timeSeries.getDateFormat());
    TimeSeriesAnalysisRequest newTimeSeriesRequest = new TimeSeriesAnalysisRequest(newTimeSeries, numberOfValues);

    return engineClient.forecast(newTimeSeriesRequest);
  }

  @Override
  public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
  }

  @Override
  public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    TimeSeriesAnalysisEngineClient engineClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);
    return engineClient.predict(timeSeriesAnalysisRequest);
  }
}
