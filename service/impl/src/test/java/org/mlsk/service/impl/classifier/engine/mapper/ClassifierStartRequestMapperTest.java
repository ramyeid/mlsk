package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierStartRequestMapper.toClassifierStartRequestModel;

public class ClassifierStartRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_start_request_model() {
    ClassifierStartRequest classifierStartRequest = buildClassifierStartRequest();

    ClassifierStartRequestModel actualStartRequestModel = toClassifierStartRequestModel(classifierStartRequest);

    assertEquals(buildExpectedModel(), actualStartRequestModel);
  }

  private static ClassifierStartRequestModel buildExpectedModel() {
    return new ClassifierStartRequestModel(11L, "predictionColumnName", newArrayList("actionColumn1", "actionColumn2"), 5);
  }

  private static ClassifierStartRequest buildClassifierStartRequest() {
    return new ClassifierStartRequest(11L, "predictionColumnName", newArrayList("actionColumn1", "actionColumn2"), 5);
  }

}