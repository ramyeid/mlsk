package org.mlsk.service.impl.classifier.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.api.mapper.ClassifierRequestMapper.fromServiceModel;

public class ClassifierRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_request() {
    ClassifierRequestModel classifierRequestModel = buildClassifierRequestModel();

    ClassifierRequest actualRequest = fromServiceModel(classifierRequestModel, ClassifierType.DECISION_TREE);

    assertEquals(buildExpectedRequest(), actualRequest);
  }

  private static ClassifierRequestModel buildClassifierRequestModel() {
    return new ClassifierRequestModel(11L);
  }

  private static ClassifierRequest buildExpectedRequest() {
    return new ClassifierRequest(11L, ClassifierType.DECISION_TREE);
  }
}