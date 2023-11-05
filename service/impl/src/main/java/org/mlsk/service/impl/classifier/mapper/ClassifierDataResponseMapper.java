package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierDataResponse;

import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierResponseModel;

public final class ClassifierDataResponseMapper {

  private ClassifierDataResponseMapper() {
  }

  public static ClassifierResponseModel toClassifierResponseModel(ClassifierDataResponse classifierDataResponse) {
    return buildClassifierResponseModel(classifierDataResponse.getColumnName(), classifierDataResponse.getValues());
  }
}