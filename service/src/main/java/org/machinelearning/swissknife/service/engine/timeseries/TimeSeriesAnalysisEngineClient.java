package org.machinelearning.swissknife.service.engine.timeseries;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.lib.rest.RestClient;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.service.engine.timeseries.exceptions.TimeSeriesAnalysisEngineRequestException;

import static org.machinelearning.swissknife.lib.endpoints.TimeSeriesAnalysisUrls.*;

public class TimeSeriesAnalysisEngineClient  implements TimeSeriesAnalysis {

    private final RestClient restClient;

    public TimeSeriesAnalysisEngineClient(ServiceInformation serviceInformation) {
        this(new RestClient(serviceInformation));
    }

    @VisibleForTesting
    TimeSeriesAnalysisEngineClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public TimeSeries forecast(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(FORECAST_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        } catch(Exception exception) {
            throw new TimeSeriesAnalysisEngineRequestException("Failed to post forecast to engine", exception);
        }
    }

    @Override
    public Double computeForecastAccuracy(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(FORECAST_ACCURACY_URL, timeSeriesAnalysisRequest, Double.class);
        } catch (Exception exception) {
            throw new TimeSeriesAnalysisEngineRequestException("Failed to post computeForecastAccuracy to engine", exception);
        }
    }

    @Override
    public TimeSeries predict(TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        try {
            return restClient.post(PREDICATE_URL, timeSeriesAnalysisRequest, TimeSeries.class);
        } catch (Exception exception) {
            throw new TimeSeriesAnalysisEngineRequestException("Failed to post predict to engine", exception);
        }
    }
}
