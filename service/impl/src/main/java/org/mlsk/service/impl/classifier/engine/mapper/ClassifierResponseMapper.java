package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierResponse;

public class ClassifierResponseMapper {

  private ClassifierResponseMapper() {
  }

  public static ClassifierResponse fromEngineModel(ClassifierResponseModel classifierResponseModel) {
    return new ClassifierResponse(
        classifierResponseModel.getRequestId(),
        classifierResponseModel.getColumnName(),
        classifierResponseModel.getValues(),
        ClassifierTypeMapper.fromEngineModel(classifierResponseModel.getClassifierType())
    );
  }
}
