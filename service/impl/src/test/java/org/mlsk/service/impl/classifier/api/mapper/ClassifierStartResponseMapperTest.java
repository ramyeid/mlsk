package org.mlsk.service.impl.classifier.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.classifier.model.ClassifierStartResponseModel;
import org.mlsk.service.model.classifier.ClassifierStartResponse;
import org.mlsk.service.model.classifier.ClassifierType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.api.mapper.ClassifierStartResponseMapper.toServiceModel;

public class ClassifierStartResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_start_response_model() {
    ClassifierStartResponse classifierStartResponse = buildClassifierStartResponse();

    ClassifierStartResponseModel actualStartResponseModel = toServiceModel(classifierStartResponse);

    assertEquals(buildExpectedStartResponseModel(), actualStartResponseModel);
  }

  private static ClassifierStartResponse buildClassifierStartResponse() {
    return new ClassifierStartResponse(10L, ClassifierType.DECISION_TREE);
  }

  private static ClassifierStartResponseModel buildExpectedStartResponseModel() {
    return new ClassifierStartResponseModel(10L);
  }
}