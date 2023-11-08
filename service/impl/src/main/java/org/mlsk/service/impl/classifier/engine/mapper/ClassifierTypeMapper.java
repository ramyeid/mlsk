package org.mlsk.service.impl.classifier.engine.mapper;

import org.mlsk.api.engine.classifier.model.ClassifierTypeModel;
import org.mlsk.service.model.classifier.ClassifierType;

public final class ClassifierTypeMapper {

  public static ClassifierTypeModel toEngineModel(ClassifierType classifierType) {
    switch (classifierType) {
      case DECISION_TREE:
        return ClassifierTypeModel.DECISION_TREE;
      default:
        throw new RuntimeException("Unhandled enum!");
    }
  }

  public static ClassifierType fromEngineModel(ClassifierTypeModel classifierType) {
    switch (classifierType) {
      case DECISION_TREE:
        return ClassifierType.DECISION_TREE;
      default:
        throw new RuntimeException("Unhandled enum!");
    }
  }
}