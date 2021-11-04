package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

public final class ClassifierRequestMapper {

  private ClassifierRequestMapper() {
  }

  public static ClassifierRequest toClassifierRequest(ClassifierRequestModel classifierRequestModel) {
    String requestId = classifierRequestModel.getRequestId();

    return new ClassifierRequest(requestId);
  }
}
