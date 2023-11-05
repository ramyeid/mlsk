package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierStartRequestModel;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierStartRequestMapper.toClassifierStartRequest;

public class ClassifierStartRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_start_request() {
    ClassifierStartRequestModel classifierStartRequestModel = buildClassifierStartRequestModel();

    ClassifierStartRequest actualStartRequest = toClassifierStartRequest(1L, classifierStartRequestModel);

    assertEquals(buildExpectedStartRequest(), actualStartRequest);
  }

  private static ClassifierStartRequestModel buildClassifierStartRequestModel() {
    ClassifierStartRequestModel classifierStartRequestModel = new ClassifierStartRequestModel();
    classifierStartRequestModel.setPredictionColumnName("predictionColumnName");
    classifierStartRequestModel.setActionColumnNames(newArrayList("col0", "col1"));
    classifierStartRequestModel.setNumberOfValues(10);
    return classifierStartRequestModel;
  }

  private static ClassifierStartRequest buildExpectedStartRequest() {
    return new ClassifierStartRequest(1L, "predictionColumnName", newArrayList("col0", "col1"), 10);
  }
}