package org.mlsk.service.impl.classifier.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.api.mapper.ClassifierStartRequestMapper.fromServiceModel;

public class ClassifierStartRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_start_request() {
    ClassifierStartRequestModel classifierStartRequestModel = buildClassifierStartRequestModel();

    ClassifierStartRequest actualStartRequest = fromServiceModel(1L, classifierStartRequestModel, ClassifierType.DECISION_TREE);

    assertEquals(buildExpectedStartRequest(), actualStartRequest);
  }

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    return new ClassifierStartRequestModel("predictionColumnName", newArrayList("col0", "col1"), 10);
  }

  private static ClassifierStartRequest buildExpectedStartRequest() {
    return new ClassifierStartRequest(1L, "predictionColumnName", newArrayList("col0", "col1"), 10, ClassifierType.DECISION_TREE);
  }
}