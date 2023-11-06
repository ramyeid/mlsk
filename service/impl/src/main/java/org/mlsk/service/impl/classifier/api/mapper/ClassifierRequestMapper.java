package org.mlsk.service.impl.classifier.api.mapper;

import org.mlsk.api.service.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

public final class ClassifierRequestMapper {

  private ClassifierRequestMapper() {
  }

  public static ClassifierRequest fromServiceModel(ClassifierRequestModel classifierRequestModel) {
    long requestId = classifierRequestModel.getRequestId();

    return new ClassifierRequest(requestId);
  }
}
