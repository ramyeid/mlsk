package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierResponseModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;

import java.util.List;

public final class ClassifierModelHelper {

  private ClassifierModelHelper() {
  }

  public static ClassifierStartResponseModel buildClassifierStartResponseModel(long requestId) {
    ClassifierStartResponseModel classifierStartResponseModel = new ClassifierStartResponseModel();
    classifierStartResponseModel.setRequestId(requestId);
    return classifierStartResponseModel;
  }

  public static ClassifierResponseModel buildClassifierResponseModel(String columnName, List<Integer> values) {
    ClassifierResponseModel classifierResponseModel = new ClassifierResponseModel();
    classifierResponseModel.setColumnName(columnName);
    classifierResponseModel.setValues(values);
    return classifierResponseModel;
  }
}