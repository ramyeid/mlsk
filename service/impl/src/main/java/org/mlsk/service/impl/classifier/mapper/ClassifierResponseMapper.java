package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.service.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierResponse;

import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierResponseModel;

public final class ClassifierResponseMapper {

  private ClassifierResponseMapper() {
  }

  public static ClassifierResponseModel toClassifierResponseModel(ClassifierResponse classifierResponse) {
    return buildClassifierResponseModel(classifierResponse.getRequestId(), classifierResponse.getColumnName(), classifierResponse.getValues());
  }
}
