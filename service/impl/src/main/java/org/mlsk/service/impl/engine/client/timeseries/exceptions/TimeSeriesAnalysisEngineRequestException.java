package org.mlsk.service.impl.engine.client.timeseries.exceptions;

public class TimeSeriesAnalysisEngineRequestException extends RuntimeException {

  public TimeSeriesAnalysisEngineRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
