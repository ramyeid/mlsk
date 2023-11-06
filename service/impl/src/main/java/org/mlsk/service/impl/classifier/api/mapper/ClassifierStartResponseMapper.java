package org.mlsk.service.impl.classifier.api.mapper;

import org.mlsk.api.service.classifier.model.ClassifierStartResponseModel;
import org.mlsk.service.model.classifier.ClassifierStartResponse;

public final class ClassifierStartResponseMapper {

  private ClassifierStartResponseMapper() {
  }

  public static ClassifierStartResponseModel toServiceModel(ClassifierStartResponse classifierStartResponse) {
    return new ClassifierStartResponseModel(classifierStartResponse.getRequestId());
  }
}