package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierRequestModel;
import org.mlsk.api.engine.classifier.model.ClassifierTypeModel;
import org.mlsk.service.model.classifier.ClassifierRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierRequestMapper.toEngineModel;

public class ClassifierRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_request() {
    ClassifierRequest classifierRequest = buildClassifierRequest();

    ClassifierRequestModel actualRequestModel = toEngineModel(classifierRequest);

    assertEquals(buildExpectedModel(), actualRequestModel);
  }

  private static ClassifierRequestModel buildExpectedModel() {
    return new ClassifierRequestModel(11L, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(11L, ClassifierType.DECISION_TREE);
  }

}