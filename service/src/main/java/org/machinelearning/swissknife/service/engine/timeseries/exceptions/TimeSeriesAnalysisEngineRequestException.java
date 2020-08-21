package org.machinelearning.swissknife.service.engine.timeseries.exceptions;

public class TimeSeriesAnalysisEngineRequestException extends RuntimeException {

    public TimeSeriesAnalysisEngineRequestException(String message, Exception cause) {
        super(message, cause);
    }
}
