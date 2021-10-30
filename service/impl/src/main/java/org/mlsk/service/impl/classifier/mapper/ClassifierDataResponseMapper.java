package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierDataResponseModel;
import org.mlsk.service.model.classifier.ClassifierDataResponse;

import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierDataResponseModel;

public final class ClassifierDataResponseMapper {

  private ClassifierDataResponseMapper() {
  }

  public static ClassifierDataResponseModel toClassifierDataResponseModel(ClassifierDataResponse classifierDataResponse) {
    return buildClassifierDataResponseModel(classifierDataResponse.getColumnName(), classifierDataResponse.getValues());
  }
}