package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierStartResponseModel;
import org.mlsk.service.model.classifier.ClassifierStartResponse;

import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierStartResponseModel;

public final class ClassifierStartResponseMapper {

  private ClassifierStartResponseMapper() {
  }

  public static ClassifierStartResponseModel toClassifierStartResponseModel(ClassifierStartResponse classifierStartResponse) {
    return buildClassifierStartResponseModel(classifierStartResponse.getRequestId());
  }
}