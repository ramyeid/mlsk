package org.mlsk.service.impl.inttest.classifier.decisiontree.helper;

import org.mlsk.api.engine.classifier.model.ClassifierCancelRequestModel;
import org.mlsk.api.service.classifier.model.*;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.api.engine.classifier.model.ClassifierTypeModel.DECISION_TREE;

public final class DecisionTreeHelper {

  private DecisionTreeHelper() {
  }

  public static ClassifierStartRequestModel buildServiceClassifierStartRequestModel() {
    return new ClassifierStartRequestModel(
        "predictionColumnName",
        newArrayList("col0", "col1"),
        2
    );
  }

  public static org.mlsk.api.engine.classifier.model.ClassifierStartRequestModel buildEngineClassifierStartRequestModel(long requestId) {
    return new org.mlsk.api.engine.classifier.model.ClassifierStartRequestModel(
        requestId,
        "predictionColumnName",
        newArrayList("col0", "col1"),
        2,
        DECISION_TREE
    );
  }

  public static ClassifierDataRequestModel buildServiceClassifierData1RequestModel(long requestId) {
    return new ClassifierDataRequestModel(
        requestId,
        "col0",
        newArrayList(1, 0, 1, 0)
    );
  }

  public static org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel buildEngineClassifierData1RequestModel(long requestId) {
    return new org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel(
        requestId,
        "col0",
        newArrayList(1, 0, 1, 0),
        DECISION_TREE
    );
  }

  public static ClassifierDataRequestModel buildServiceClassifierData2RequestModel(long requestId) {
    return new ClassifierDataRequestModel(
        requestId,
        "col1",
        newArrayList(0, 0, 0, 0)
    );
  }

  public static org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel buildEngineClassifierData2RequestModel(long requestId) {
    return new org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel(
        requestId,
        "col1",
        newArrayList(0, 0, 0, 0),
        DECISION_TREE
    );
  }

  public static ClassifierRequestModel buildServiceClassifierRequestModel(long requestId) {
    return new ClassifierRequestModel(requestId);
  }

  public static org.mlsk.api.engine.classifier.model.ClassifierRequestModel buildEngineClassifierRequestModel(long requestId) {
    return new org.mlsk.api.engine.classifier.model.ClassifierRequestModel(requestId, DECISION_TREE);
  }

  public static org.mlsk.api.engine.classifier.model.ClassifierResponseModel buildEngineClassifierResponseModel(long requestId) {
    return new org.mlsk.api.engine.classifier.model.ClassifierResponseModel(
        requestId,
        "predictionColumnName",
        newArrayList(1, 1),
        DECISION_TREE);
  }

  public static ClassifierStartResponseModel buildServiceClassifierStartResponseModel(long requestId) {
    return new ClassifierStartResponseModel(requestId);
  }

  public static ClassifierResponseModel buildServiceClassifierResponseModel(long requestId) {
    return new ClassifierResponseModel(requestId, "predictionColumnName", newArrayList(1, 1));
  }

  public static ClassifierCancelRequestModel buildEngineClassifierCancelRequestModel(long requestId) {
    return new ClassifierCancelRequestModel(requestId, DECISION_TREE);
  }

  public static Object buildDefaultResponse() {
    return new Object();
  }

  public static void assertOnClassifierServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(ClassifierServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}