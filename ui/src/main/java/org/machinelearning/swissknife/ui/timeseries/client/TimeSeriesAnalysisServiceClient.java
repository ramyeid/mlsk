package org.machinelearning.swissknife.ui.timeseries.client;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.ServiceInformation;
import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;

import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

public class TimeSeriesAnalysisServiceClient implements TimeSeriesAnalysis {

    private final RestClient restClient;

    public TimeSeriesAnalysisServiceClient(ServiceInformation serviceInformation) {
        this.restClient = new RestClient(serviceInformation);
    }

    @VisibleForTesting
    public TimeSeriesAnalysisServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(FORECAST_URL, timeSeriesAnalysisRequest ,TimeSeries.class);
        } catch(Exception exception) {
            throw new TimeSeriesAnalysisServiceRequestException("Failed to post forecast to service", exception);
        }
    }

    @Override
    public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
        } catch (Exception exception) {
            throw new TimeSeriesAnalysisServiceRequestException("Failed to post computeForecastAccuracy to service", exception);
        }
    }

    @Override
    public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        } catch (Exception exception) {
            throw new TimeSeriesAnalysisServiceRequestException("Failed to post predict to service", exception);
        }
    }
}
