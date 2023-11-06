package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.service.classifier.model.ClassifierResponseModel;
import org.mlsk.api.service.classifier.model.ClassifierStartResponseModel;

import java.util.List;

public final class ClassifierModelHelper {

  private ClassifierModelHelper() {
  }

  public static ClassifierStartResponseModel buildClassifierStartResponseModel(long requestId) {
    return new ClassifierStartResponseModel(requestId);
  }

  public static ClassifierResponseModel buildClassifierResponseModel(long requestId, String columnName, List<Integer> values) {
    return new ClassifierResponseModel(requestId, columnName, values);
  }
}