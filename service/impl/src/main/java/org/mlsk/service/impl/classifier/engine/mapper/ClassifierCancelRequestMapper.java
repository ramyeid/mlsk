package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierCancelRequestModel;
import org.mlsk.service.model.classifier.ClassifierCancelRequest;

public class ClassifierCancelRequestMapper {

  private ClassifierCancelRequestMapper() {
  }

  public static ClassifierCancelRequestModel toEngineModel(ClassifierCancelRequest classifierCancelRequest) {
    return new ClassifierCancelRequestModel(
        classifierCancelRequest.getRequestId(),
        ClassifierTypeMapper.toEngineModel(classifierCancelRequest.getClassifierType())
    );
  }
}
