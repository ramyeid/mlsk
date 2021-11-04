package org.mlsk.service.impl.classifier.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ClassifierServiceException extends RuntimeException {

  public ClassifierServiceException(String message) {
    super(message);
  }

}