package org.mlsk.service.impl.classifier.api.mapper;

import org.mlsk.api.service.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierResponse;

public final class ClassifierResponseMapper {

  private ClassifierResponseMapper() {
  }

  public static ClassifierResponseModel toClassifierResponseModel(ClassifierResponse classifierResponse) {
    return new ClassifierResponseModel(classifierResponse.getRequestId(), classifierResponse.getColumnName(), classifierResponse.getValues());
  }
}
