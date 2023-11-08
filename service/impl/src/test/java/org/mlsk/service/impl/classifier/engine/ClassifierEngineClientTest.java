package org.mlsk.service.impl.classifier.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.engine.classifier.decisiontree.client.DecisionTreeEngineApi;
import org.mlsk.api.engine.classifier.model.*;
import org.mlsk.service.classifier.ClassifierType;
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
  private DecisionTreeEngineApi decisionTreeEngineApi;

  private ClassifierEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new ClassifierEngineClient(decisionTreeEngineApi);
  }

  @Test
  public void should_delegate_start_call_to_engine() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;

    client.start(classifierStartRequest, classifierType);

    verify(decisionTreeEngineApi).start(buildClassifierStartRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_start_failure() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(new InvalidParameterException()).when(decisionTreeEngineApi).start(any());

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
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(buildHttpServerErrorException("Original Start Exception Message")).when(decisionTreeEngineApi).start(any());

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
    ClassifierType classifierType = ClassifierType.DECISION_TREE;

    client.data(classifierDataRequest, classifierType);

    verify(decisionTreeEngineApi).data(buildClassifierDataRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_data_failure() {
    ClassifierDataRequest classifierDataRequest = buildClassifierDataRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(new InvalidParameterException()).when(decisionTreeEngineApi).data(any());

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
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(buildHttpServerErrorException("Original Data Exception Message")).when(decisionTreeEngineApi).data(any());

    try {
      client.data(classifierDataRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post data to engine: Original Data Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    when(decisionTreeEngineApi.predict(buildClassifierRequestModel())).thenReturn(buildClassifierResponseModel());

    ClassifierResponse actualResponse = client.predict(classifierRequest, classifierType);

    verify(decisionTreeEngineApi).predict(buildClassifierRequestModel());
    assertEquals(buildClassifierResponse(), actualResponse);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_failure() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(new InvalidParameterException()).when(decisionTreeEngineApi).predict(any());

    try {
      client.predict(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(buildHttpServerErrorException("Original Predict Exception Message")).when(decisionTreeEngineApi).predict(any());

    try {
      client.predict(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict to engine: Original Predict Exception Message");
    }
  }

  @Test
  public void should_delegate_predict_accuracy_call_to_engine() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    when(decisionTreeEngineApi.computePredictAccuracy(buildClassifierRequestModel())).thenReturn(valueOf(123.2));

    Double actualAccuracy = client.computePredictAccuracy(classifierRequest, classifierType);

    verify(decisionTreeEngineApi).computePredictAccuracy(buildClassifierRequestModel());
    assertEquals(123.2, actualAccuracy);
  }

  @Test
  public void should_rethrow_classifier_exception_on_predict_accuracy_failure() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(new InvalidParameterException()).when(decisionTreeEngineApi).computePredictAccuracy(any());

    try {
      client.computePredictAccuracy(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post predict accuracy to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_predict_accuracy_failure_with_http_server_error_exception() {
    ClassifierRequest classifierRequest = buildClassifierRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(buildHttpServerErrorException("Original Predict Accuracy Exception Message")).when(decisionTreeEngineApi).computePredictAccuracy(any());

    try {
      client.computePredictAccuracy(classifierRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestExceptionWithServerError(exception, "Failed on post predict accuracy to engine: Original Predict Accuracy Exception Message");
    }
  }

  @Test
  public void should_delegate_cancel_call_to_engine() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;

    client.cancel(classifierCancelRequest, classifierType);

    verify(decisionTreeEngineApi).cancel(buildClassifierCancelRequestModel());
  }

  @Test
  public void should_rethrow_classifier_exception_on_cancel_failure() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(new InvalidParameterException()).when(decisionTreeEngineApi).cancel(any());

    try {
      client.cancel(classifierCancelRequest, classifierType);
      fail("should fail");

    } catch (Exception exception) {
      assertOnClassifierEngineRequestException(exception, "Failed to post cancel to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_cancel_failure_with_http_server_error_exception() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();
    ClassifierType classifierType = ClassifierType.DECISION_TREE;
    doThrow(buildHttpServerErrorException("Original Cancel Exception Message")).when(decisionTreeEngineApi).cancel(any());

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

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel(REQUEST_ID, "predictionColumnName", newArrayList("col0", "col1"), 1);
  }

  private static ClassifierDataRequest buildClassifierDataRequest() {
    return new ClassifierDataRequest(REQUEST_ID, "columnName", newArrayList(1, 0, 1));
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel() {
    return new ClassifierDataRequestModel(REQUEST_ID, "columnName", newArrayList(1, 0, 1));
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(REQUEST_ID);
  }

  private static ClassifierRequestModel buildClassifierRequestModel() {
    return new ClassifierRequestModel(REQUEST_ID);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(REQUEST_ID, "columnName", newArrayList(0, 1));
  }

  private static ClassifierResponseModel buildClassifierResponseModel() {
    return new ClassifierResponseModel(REQUEST_ID, "columnName", newArrayList(0, 1));
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(REQUEST_ID);
  }

  private static ClassifierCancelRequestModel buildClassifierCancelRequestModel() {
    return new ClassifierCancelRequestModel(REQUEST_ID);
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