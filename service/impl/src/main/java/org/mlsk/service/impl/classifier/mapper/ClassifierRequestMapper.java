package org.mlsk.service.impl.classifier.mapper;

import org.mlsk.api.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

import static java.lang.Long.parseLong;

public final class ClassifierRequestMapper {

  private ClassifierRequestMapper() {
  }

  public static ClassifierRequest toClassifierRequest(ClassifierRequestModel classifierRequestModel) {
    long requestId = parseLong(classifierRequestModel.getRequestId());

    return new ClassifierRequest(requestId);
  }
}
