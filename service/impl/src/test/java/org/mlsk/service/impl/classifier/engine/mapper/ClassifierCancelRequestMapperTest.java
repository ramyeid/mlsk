package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierCancelRequestModel;
import org.mlsk.api.engine.classifier.model.ClassifierTypeModel;
import org.mlsk.service.model.classifier.ClassifierCancelRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierCancelRequestMapper.toEngineModel;

public class ClassifierCancelRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_cancel_request_model() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();

    ClassifierCancelRequestModel actualCancelRequestModel = toEngineModel(classifierCancelRequest);

    assertEquals(buildExpectedModel(), actualCancelRequestModel);
  }

  private static ClassifierCancelRequestModel buildExpectedModel() {
    return new ClassifierCancelRequestModel(11L, ClassifierTypeModel.DECISION_TREE);
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(11L, ClassifierType.DECISION_TREE);
  }

}