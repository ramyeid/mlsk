package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierCancelRequestModel;
import org.mlsk.service.model.classifier.ClassifierCancelRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierCancelRequestMapper.toClassifierCancelRequestModel;

public class ClassifierCancelRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_cancel_request_model() {
    ClassifierCancelRequest classifierCancelRequest = buildClassifierCancelRequest();

    ClassifierCancelRequestModel actualCancelRequestModel = toClassifierCancelRequestModel(classifierCancelRequest);

    assertEquals(buildExpectedModel(), actualCancelRequestModel);
  }

  private static ClassifierCancelRequestModel buildExpectedModel() {
    return new ClassifierCancelRequestModel(11L);
  }

  private static ClassifierCancelRequest buildClassifierCancelRequest() {
    return new ClassifierCancelRequest(11L);
  }

}