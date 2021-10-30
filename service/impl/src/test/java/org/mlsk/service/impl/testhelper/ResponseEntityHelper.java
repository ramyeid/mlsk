package org.mlsk.service.impl.testhelper;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public final class ResponseEntityHelper {

  private ResponseEntityHelper() {
  }

  public static <T> void assertOnResponseEntity(T expectedBody, ResponseEntity<T> actualResponse) {
    assertEquals(OK, actualResponse.getStatusCode());
    assertEquals(expectedBody, actualResponse.getBody());
  }
}