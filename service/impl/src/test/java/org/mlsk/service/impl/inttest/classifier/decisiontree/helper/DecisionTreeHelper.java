package org.mlsk.service.impl.inttest.classifier.decisiontree.helper;

import org.mlsk.api.classifier.model.*;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

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
    ClassifierStartRequestModel classifierStartRequestModel = new ClassifierStartRequestModel();
    classifierStartRequestModel.setPredictionColumnName("predictionColumnName");
    classifierStartRequestModel.setActionColumnNames(newArrayList("col0", "col1"));
    classifierStartRequestModel.setNumberOfValues(2);
    return classifierStartRequestModel;
  }

  public static ClassifierStartRequest buildClassifierStartRequest(long requestId) {
    return new ClassifierStartRequest(requestId, "predictionColumnName", newArrayList("col0", "col1"), 2);
  }

  public static ClassifierDataRequestModel buildClassifierData1RequestModel(long requestId) {
    ClassifierDataRequestModel classifierDataRequestModel = new ClassifierDataRequestModel();
    classifierDataRequestModel.setRequestId(requestId);
    classifierDataRequestModel.setColumnName("col0");
    classifierDataRequestModel.setValues(newArrayList(1, 0, 1, 0));
    return classifierDataRequestModel;
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
    ClassifierRequestModel classifierRequestModel = new ClassifierRequestModel();
    classifierRequestModel.setRequestId(requestId);
    return classifierRequestModel;
  }

  public static ClassifierDataResponse buildClassifierDataResponse() {
    return new ClassifierDataResponse("predictionColumnName", newArrayList(1, 1));
  }

  public static ClassifierStartResponseModel buildClassifierStartResponseModel(long requestId) {
    ClassifierStartResponseModel classifierStartResponseModel = new ClassifierStartResponseModel();
    classifierStartResponseModel.setRequestId(requestId);
    return classifierStartResponseModel;
  }

  public static ClassifierDataResponseModel buildClassifierDataResponseModel() {
    ClassifierDataResponseModel classifierDataResponseModel = new ClassifierDataResponseModel();
    classifierDataResponseModel.setColumnName("predictionColumnName");
    classifierDataResponseModel.setValues(newArrayList(1, 1));
    return classifierDataResponseModel;
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
