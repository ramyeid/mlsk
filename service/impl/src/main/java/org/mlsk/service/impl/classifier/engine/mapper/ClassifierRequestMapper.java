package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

public final class ClassifierRequestMapper {

  private ClassifierRequestMapper() {
  }

  public static ClassifierRequestModel toClassifierRequestModel(ClassifierRequest classifierRequest) {
    return new ClassifierRequestModel(classifierRequest.getRequestId());
  }
}
