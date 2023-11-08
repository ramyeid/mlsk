package org.mlsk.service.impl.classifier.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierResponse;
import org.mlsk.service.model.classifier.ClassifierType;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.api.mapper.ClassifierResponseMapper.toServiceModel;

public class ClassifierResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_response_model() {
    ClassifierResponse classifierResponse = buildClassifierResponse();

    ClassifierResponseModel actualResponseModel = toServiceModel(classifierResponse);

    assertEquals(buildExpectedResponseModel(), actualResponseModel);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(1L, "columnName", newArrayList(1, 2, 3), ClassifierType.DECISION_TREE);
  }

  private static ClassifierResponseModel buildExpectedResponseModel() {
    return new ClassifierResponseModel(1L, "columnName", newArrayList(1, 2, 3));
  }
}