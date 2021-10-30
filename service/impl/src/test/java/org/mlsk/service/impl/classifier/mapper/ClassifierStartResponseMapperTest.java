package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;
import org.mlsk.service.model.classifier.ClassifierStartResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierStartResponseMapper.toClassifierStartResponseModel;

public class ClassifierStartResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_start_response_model() {
    ClassifierStartResponse classifierStartResponse = buildClassifierStartResponse();

    ClassifierStartResponseModel actualStartResponseModel = toClassifierStartResponseModel(classifierStartResponse);

    assertEquals(buildExpectedStartResponseModel(), actualStartResponseModel);
  }

  private static ClassifierStartResponse buildClassifierStartResponse() {
    return new ClassifierStartResponse("requestId");
  }

  private static ClassifierStartResponseModel buildExpectedStartResponseModel() {
    ClassifierStartResponseModel classifierStartResponseModel = new ClassifierStartResponseModel();
    classifierStartResponseModel.setRequestId("requestId");
    return classifierStartResponseModel;
  }
}