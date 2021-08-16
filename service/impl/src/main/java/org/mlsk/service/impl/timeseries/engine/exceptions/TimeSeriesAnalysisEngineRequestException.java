package org.mlsk.service.impl.timeseries.engine.exceptions;

public class TimeSeriesAnalysisEngineRequestException extends RuntimeException {

  public TimeSeriesAnalysisEngineRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
