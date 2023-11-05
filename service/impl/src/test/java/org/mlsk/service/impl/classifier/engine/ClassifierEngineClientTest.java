package org.mlsk.service.impl.classifier.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.testhelper.RestClientHelper.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClassifierEngineClientTest {

  private static final long REQUEST_ID = 10L;

  @Mock
  private RestClient restClient;

  private ClassifierEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new ClassifierEngineClient(restClient);
  }

  @Test
  public void should_delegate_start_call_to_engine() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getStartUrl()).thenReturn("startUrl");

    client.start(classifierStartRequest, classifierType);

    verifyPostWithBodyWithoutResponseCalled(restClient, "startUrl", classifierStartRequest);
  }

  @Test
  public void should_rethrow_classifier_exception_on_start_failure() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getStartUrl()).thenReturn("startUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "startUrl", classifierStartRequest, new InvalidParameterException());

    try {
      client.start(classifierStartRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post start to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_start_failure_with_http_server_error_exception() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getStartUrl()).thenReturn("startUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "startUrl", classifierStartRequest, buildHttpServerErrorException("Original Start Exception Message"));

    try {
      client.start(classifierStartRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post start to engine: Original Start Exception Message");
    }
  }

  @Test
  public void should_delegate_data_call_to_engine() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getDataUrl()).thenReturn("dataUrl");

    client.data(classifierDataRequest, classifierType);

    verifyPostWithBodyWithoutResponseCalled(restClient, "dataUrl", classifierDataRequest);
  }

  @Test
  public void should_rethrow_classifier_exception_on_data_failure() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getDataUrl()).thenReturn("dataUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "dataUrl", classifierDataRequest, new InvalidParameterException());

    try {
      client.data(classifierDataRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post data to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_data_failure_with_http_server_error_exception() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getDataUrl()).thenReturn("dataUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "dataUrl", classifierDataRequest, buildHttpServerErrorException("Original Data Exception Message"));

    try {
      client.data(classifierDataRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post data to engine: Original Data Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    onPostWithoutBodyWithResponseReturn(restClient, "predictUrl", ClassifierDataResponse.class, buildClassifierDataResponse());

    ClassifierDataResponse actualResponse = client.predict(classifierType);

    verifyPostWithoutBodyWithResponseCalled(restClient, "predictUrl", ClassifierDataResponse.class);
    assertEquals(buildClassifierDataResponse(), actualResponse);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_failure() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    doThrowExceptionOnPostWithoutBodyWithResponse(restClient, "predictUrl", ClassifierDataResponse.class, new InvalidParameterException());

    try {
      client.predict(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    doThrowExceptionOnPostWithoutBodyWithResponse(restClient, "predictUrl", ClassifierDataResponse.class, buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_accuracy_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    onPostWithoutBodyWithResponseReturn(restClient, "predictAccuracyUrl", Double.class, 123.2);

    Double actualAccuracy = client.computePredictAccuracy(classifierType);

    verifyPostWithoutBodyWithResponseCalled(restClient, "predictAccuracyUrl", Double.class);
    assertEquals(123.2, actualAccuracy);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_accuracy_failure() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    doThrowExceptionOnPostWithoutBodyWithResponse(restClient, "predictAccuracyUrl", Double.class, new InvalidParameterException());

    try {
      client.computePredictAccuracy(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict accuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_accuracy_failure_with_http_server_error_exception() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    doThrowExceptionOnPostWithoutBodyWithResponse(restClient, "predictAccuracyUrl", Double.class, buildHttpServerErrorException("Original Predict Accuracy Exception Message"));

    try {
      client.computePredictAccuracy(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict accuracy to engine: Original Predict Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_cancel_call_to_engine() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");

    client.cancel(classifierType);

    verifyPostWithoutBodyWithoutResponseCalled(restClient, "cancelUrl");
  }

  @Test
  public void should_rethrow_classifier_exception_on_cancel_failure() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");
    doThrowExceptionOnPostWithoutBodyWithoutResponse(restClient, "cancelUrl", new InvalidParameterException());

    try {
      client.cancel(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post cancel to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_cancel_failure_with_http_server_error_exception() {
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");
    doThrowExceptionOnPostWithoutBodyWithoutResponse(restClient, "cancelUrl", buildHttpServerErrorException("Original Cancel Exception Message"));

    try {
      client.cancel(classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post cancel to engine: Original Cancel Exception Message");
    }
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 1);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(1, 0, 1));
  }

  private static ClassifierDataResponse buildClassifierDataResponse() {
    return new ClassifierDataResponse("columnName", newArrayList(0, 1));
  }

  private static HttpServerErrorException buildHttpServerErrorException(String exceptionMessage) {
    return new HttpServerErrorException("message", HttpStatus.INTERNAL_SERVER_ERROR, "status", null, exceptionMessage.getBytes(), null);
  }

  private static void assertOnClassifierEngineRequestException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(InvalidParameterException.class, exception.getCause());
  }

  private static void assertOnClassifierEngineRequestExceptionWithServerError(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(HttpServerErrorException.class, exception.getCause());
  }
}