package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;

public final class ClassifierDataRequestMapper {

  private ClassifierDataRequestMapper() {
  }

  public static ClassifierDataRequestModel toEngineModel(ClassifierDataRequest classifierDataRequest) {
    return new ClassifierDataRequestModel(classifierDataRequest.getRequestId(), classifierDataRequest.getColumnName(), classifierDataRequest.getValues());
  }
}
