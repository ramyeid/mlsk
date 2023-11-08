package org.mlsk.service.impl.classifier.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.engine.classifier.client.ClassifierEngineApi;
import org.mlsk.api.engine.classifier.model.*;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassifierEngineClientTest {

  private static final long REQUEST_ID = 10L;

  @Mock
  private ClassifierEngineApi classifierEngineApi;

  private ClassifierEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new ClassifierEngineClient(classifierEngineApi);
  }

  @Test
  public void should_delegate_start_call_to_engine() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();

    client.start(classifierStartRequest);

    verify(classifierEngineApi).start(buildClassifierStartRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_start_failure() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    doThrow(new InvalidParameterException()).when(classifierEngineApi).start(any());

    try {
      client.start(classifierStartRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post start to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_start_failure_with_http_server_error_exception() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    doThrow(buildHttpServerErrorException("Original Start Exception Message")).when(classifierEngineApi).start(any());

    try {
      client.start(classifierStartRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post start to engine: Original Start Exception Message");
    }
  }

  @Test
  public void should_delegate_data_call_to_engine() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();

    client.data(classifierDataRequest);

    verify(classifierEngineApi).data(buildClassifierDataRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_data_failure() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    doThrow(new InvalidParameterException()).when(classifierEngineApi).data(any());

    try {
      client.data(classifierDataRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post data to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_data_failure_with_http_server_error_exception() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    doThrow(buildHttpServerErrorException("Original Data Exception Message")).when(classifierEngineApi).data(any());

    try {
      client.data(classifierDataRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post data to engine: Original Data Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    when(classifierEngineApi.predict(buildClassifierRequestModel())).thenReturn(buildClassifierResponseModel());

    ClassifierResponse actualResponse = client.predict(classifierRequest);

    verify(classifierEngineApi).predict(buildClassifierRequestModel());
    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_failure() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    doThrow(new InvalidParameterException()).when(classifierEngineApi).predict(any());

    try {
      client.predict(classifierRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    doThrow(buildHttpServerErrorException("Original Predict Exception Message")).when(classifierEngineApi).predict(any());

    try {
      client.predict(classifierRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_accuracy_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    when(classifierEngineApi.computePredictAccuracy(buildClassifierRequestModel())).thenReturn(valueOf(123.2));

    Double actualAccuracy = client.computePredictAccuracy(classifierRequest);

    verify(classifierEngineApi).computePredictAccuracy(buildClassifierRequestModel());
    assertEquals(123.2, actualAccuracy);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_accuracy_failure() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    doThrow(new InvalidParameterException()).when(classifierEngineApi).computePredictAccuracy(any());

    try {
      client.computePredictAccuracy(classifierRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict accuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_accuracy_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    doThrow(buildHttpServerErrorException("Original Predict Accuracy Exception Message")).when(classifierEngineApi).computePredictAccuracy(any());

    try {
      client.computePredictAccuracy(classifierRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict accuracy to engine: Original Predict Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_cancel_call_to_engine() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();

    client.cancel(classifierCancelRequest);

    verify(classifierEngineApi).cancel(buildClassifierCancelRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_cancel_failure() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    doThrow(new InvalidParameterException()).when(classifierEngineApi).cancel(any());

    try {
      client.cancel(classifierCancelRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post cancel to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_cancel_failure_with_http_server_error_exception() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    doThrow(buildHttpServerErrorException("Original Cancel Exception Message")).when(classifierEngineApi).cancel(any());

    try {
      client.cancel(classifierCancelRequest);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post cancel to engine: Original Cancel Exception Message");
    }
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 1, ClassifierType.DECISION_TREE);
  }

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 1, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(1, 0, 1), ClassifierType.DECISION_TREE);
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel() {
    return new ClassifierDataRequestModel(REQUEST_ID, "columnName", newArrayList(1, 0, 1), ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(REQUEST_ID, ClassifierType.DECISION_TREE);
  }

  private static ClassifierRequestModel buildClassifierRequestModel() {
    return new ClassifierRequestModel(REQUEST_ID, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1), ClassifierType.DECISION_TREE);
  }

  private static ClassifierResponseModel buildClassifierResponseModel() {
    return new ClassifierResponseModel(REQUEST_ID, "columnName", newArrayList(0, 1), ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(REQUEST_ID, ClassifierType.DECISION_TREE);
  }

  private static ClassifierCancelRequestModel buildClassifierCancelRequestModel() {
    return new ClassifierCancelRequestModel(REQUEST_ID, ClassifierTypeModel.DECISION_TREE);
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