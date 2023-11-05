package org.mlsk.service.impl.classifier.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.*;
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
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    onPostWithBodyWithResponseReturn(restClient, "predictUrl", classifierRequest, ClassifierResponse.class, buildClassifierResponse());

    ClassifierResponse actualResponse = client.predict(classifierRequest, classifierType);

    verifyPostWithBodyWithResponseCalled(restClient, "predictUrl", classifierRequest, ClassifierResponse.class);
    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_failure() {
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    doThrowExceptionOnPostWithBodyWithResponse(restClient, "predictUrl", classifierRequest, ClassifierResponse.class, new InvalidParameterException());

    try {
      client.predict(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictUrl()).thenReturn("predictUrl");
    doThrowExceptionOnPostWithBodyWithResponse(restClient, "predictUrl", classifierRequest, ClassifierResponse.class, buildHttpServerErrorException("Original Predict Exception Message"));

    try {
      client.predict(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_accuracy_call_to_engine() {
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    onPostWithBodyWithResponseReturn(restClient, "predictAccuracyUrl", classifierRequest, Double.class, 123.2);

    Double actualAccuracy = client.computePredictAccuracy(classifierRequest, classifierType);

    verifyPostWithBodyWithResponseCalled(restClient, "predictAccuracyUrl", classifierRequest, Double.class);
    assertEquals(123.2, actualAccuracy);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_accuracy_failure() {
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    doThrowExceptionOnPostWithBodyWithResponse(restClient, "predictAccuracyUrl", classifierRequest, Double.class, new InvalidParameterException());

    try {
      client.computePredictAccuracy(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict accuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_accuracy_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = new ClassifierRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getPredictAccuracyUrl()).thenReturn("predictAccuracyUrl");
    doThrowExceptionOnPostWithBodyWithResponse(restClient, "predictAccuracyUrl", classifierRequest, Double.class, buildHttpServerErrorException("Original Predict Accuracy Exception Message"));

    try {
      client.computePredictAccuracy(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict accuracy to engine: Original Predict Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_cancel_call_to_engine() {
    ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");

    client.cancel(classifierCancelRequest, classifierType);

    verifyPostWithBodyWithoutResponseCalled(restClient, "cancelUrl", classifierCancelRequest);
  }

  @Test
  public void should_rethrow_classifier_exception_on_cancel_failure() {
    ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "cancelUrl", classifierCancelRequest, new InvalidParameterException());

    try {
      client.cancel(classifierCancelRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post cancel to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_cancel_failure_with_http_server_error_exception() {
    ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(REQUEST_ID);
    ClassifierType classifierType = mock(ClassifierType.class);
    when(classifierType.getCancelUrl()).thenReturn("cancelUrl");
    doThrowExceptionOnPostWithBodyWithoutResponse(restClient, "cancelUrl", classifierCancelRequest, buildHttpServerErrorException("Original Cancel Exception Message"));

    try {
      client.cancel(classifierCancelRequest, classifierType);
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

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1));
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