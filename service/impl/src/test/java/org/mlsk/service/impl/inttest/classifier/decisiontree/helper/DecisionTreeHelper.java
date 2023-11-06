package org.mlsk.service.impl.inttest.classifier.decisiontree.helper;

import org.mlsk.api.service.classifier.model.*;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.model.classifier.*;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class DecisionTreeHelper {

  private DecisionTreeHelper() {
  }

  public static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel("predictionColumnName", newArrayList("col0", "col1"), 2);
  }

  public static ClassifierStartRequest buildClassifierStartRequest(long requestId) {
    return new ClassifierStartRequest(requestId, "predictionColumnName", newArrayList("col0", "col1"), 2);
  }

  public static ClassifierDataRequestModel buildClassifierData1RequestModel(long requestId) {
    return new ClassifierDataRequestModel(requestId, "col0", newArrayList(1, 0, 1, 0));
  }

  public static ClassifierDataRequest buildClassifierData1Request(long requestId) {
    return new ClassifierDataRequest(requestId, "col0", newArrayList(1, 0, 1, 0));
  }

  public static ClassifierDataRequestModel buildClassifierData2RequestModel(long requestId) {
    ClassifierDataRequestModel classifierDataRequestModel = new ClassifierDataRequestModel();
    classifierDataRequestModel.setRequestId(requestId);
    classifierDataRequestModel.setColumnName("col1");
    classifierDataRequestModel.setValues(newArrayList(0, 0, 0, 0));
    return classifierDataRequestModel;
  }

  public static ClassifierDataRequest buildClassifierData2Request(long requestId) {
    return new ClassifierDataRequest(requestId, "col1", newArrayList(0, 0, 0, 0));
  }

  public static ClassifierRequestModel buildClassifierRequestModel(long requestId) {
    return new ClassifierRequestModel(requestId);
  }

  public static ClassifierRequest buildClassifierRequest(long requestId) {
    return new ClassifierRequest(requestId);
  }

  public static ClassifierResponse buildClassifierResponse(long requestId) {
    return new ClassifierResponse(requestId, "predictionColumnName", newArrayList(1, 1));
  }

  public static ClassifierStartResponseModel buildClassifierStartResponseModel(long requestId) {
    return new ClassifierStartResponseModel(requestId);
  }

  public static ClassifierResponseModel buildClassifierResponseModel(long requestId) {
    return new ClassifierResponseModel(requestId, "predictionColumnName", newArrayList(1, 1));
  }

  public static ClassifierCancelRequest buildClassifierCancelRequest(long requestId) {
    return new ClassifierCancelRequest(requestId);
  }

  public static Map<String, String> buildDefaultResponse() {
    HashMap<String, String> defaultResponseMap = newHashMap();
    defaultResponseMap.put("Status", "Ok");
    return defaultResponseMap;
  }

  public static void assertOnClassifierServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}
