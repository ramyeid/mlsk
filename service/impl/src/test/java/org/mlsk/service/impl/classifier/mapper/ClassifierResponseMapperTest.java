package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierResponseModel;
import org.mlsk.service.model.classifier.ClassifierResponse;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierResponseMapper.toClassifierResponseModel;

public class ClassifierResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_response_model() {
    ClassifierResponse classifierResponse = buildClassifierResponse();

    ClassifierResponseModel actualResponseModel = toClassifierResponseModel(classifierResponse);

    assertEquals(buildExpectedResponseModel(), actualResponseModel);
  }

  private static ClassifierResponse buildClassifierResponse() {
    return new ClassifierResponse(1L, "columnName", newArrayList(1, 2, 3));
  }

  private static ClassifierResponseModel buildExpectedResponseModel() {
    ClassifierResponseModel classifierResponseModel = new ClassifierResponseModel();
    classifierResponseModel.setRequestId(1L);
    classifierResponseModel.setColumnName("columnName");
    classifierResponseModel.setValues(newArrayList(1, 2, 3));
    return classifierResponseModel;
  }
}