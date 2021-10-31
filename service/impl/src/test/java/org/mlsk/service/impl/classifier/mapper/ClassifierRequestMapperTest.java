package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierRequestModel;
import org.mlsk.service.model.classifier.ClassifierRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierRequestMapper.toClassifierRequest;

public class ClassifierRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_request() {
    ClassifierRequestModel classifierRequestModel = buildClassifierRequestModel();

    ClassifierRequest actualRequest = toClassifierRequest(classifierRequestModel);

    assertEquals(buildExpectedRequest(), actualRequest);
  }

  private static ClassifierRequestModel buildClassifierRequestModel() {
    return new ClassifierRequestModel().requestId("requestId");
  }

  private static ClassifierRequest buildExpectedRequest() {
    return new ClassifierRequest("requestId");
  }
}