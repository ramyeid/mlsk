package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierDataResponseModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;

import java.util.List;

public final class ClassifierModelHelper {

  private ClassifierModelHelper() {
  }

  public static ClassifierStartResponseModel buildClassifierStartResponseModel(String requestId) {
    ClassifierStartResponseModel classifierStartResponseModel = new ClassifierStartResponseModel();
    classifierStartResponseModel.setRequestId(requestId);
    return classifierStartResponseModel;
  }

  public static ClassifierDataResponseModel buildClassifierDataResponseModel(String columnName, List<Integer> values) {
    ClassifierDataResponseModel classifierDataResponseModel = new ClassifierDataResponseModel();
    classifierDataResponseModel.setColumnName(columnName);
    classifierDataResponseModel.setValues(values);
    return classifierDataResponseModel;
  }
}