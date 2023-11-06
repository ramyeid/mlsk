package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierRequestMapper.toClassifierRequestModel;

public class ClassifierRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_request() {
    ClassifierRequest classifierRequest = buildClassifierRequest();

    ClassifierRequestModel actualRequestModel = toClassifierRequestModel(classifierRequest);

    assertEquals(buildExpectedModel(), actualRequestModel);
  }

  private static ClassifierRequestModel buildExpectedModel() {
    return new ClassifierRequestModel(11L);
  }

  private static ClassifierRequest buildClassifierRequest() {
    return new ClassifierRequest(11L);
  }

}