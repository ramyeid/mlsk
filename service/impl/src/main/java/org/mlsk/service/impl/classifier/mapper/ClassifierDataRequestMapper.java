package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;

import java.util.List;

public final class ClassifierDataRequestMapper {

  private ClassifierDataRequestMapper() {
  }

  public static ClassifierDataRequest toClassifierDataRequest(ClassifierDataRequestModel classifierDataRequestModel) {
    String columnName = classifierDataRequestModel.getColumnName();
    List<Integer> values = classifierDataRequestModel.getValues();
    String requestId = classifierDataRequestModel.getRequestId();

    return new ClassifierDataRequest(requestId, columnName, values);
  }
}