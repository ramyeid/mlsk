package org.machinelearning.swissknife.service.engine.client.exception;

public class TimeSeriesAnalysisEngineRequestException extends RuntimeException {

    public TimeSeriesAnalysisEngineRequestException(String message, Exception cause) {
        super(message, cause);
    }
}
