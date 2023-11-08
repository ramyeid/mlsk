package org.mlsk.service.impl.classifier.api.mapper;

import org.mlsk.api.service.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import java.util.List;

public final class ClassifierDataRequestMapper {

  private ClassifierDataRequestMapper() {
  }

  public static ClassifierDataRequest fromServiceModel(ClassifierDataRequestModel classifierDataRequestModel, ClassifierType classifierType) {
    String columnName = classifierDataRequestModel.getColumnName();
    List<Integer> values = classifierDataRequestModel.getValues();
    long requestId = classifierDataRequestModel.getRequestId();

    return new ClassifierDataRequest(requestId, columnName, values, classifierType);
  }
}