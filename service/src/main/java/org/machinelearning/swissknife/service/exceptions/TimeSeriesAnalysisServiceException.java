package org.machinelearning.swissknife.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TimeSeriesAnalysisServiceException extends RuntimeException {

    public TimeSeriesAnalysisServiceException(String message) {
        super(message);
    }
}
