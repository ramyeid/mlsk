package org.mlsk.service.impl.admin.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AdminServiceException extends RuntimeException {

  public AdminServiceException(String message) {
    super(message);
  }
}
