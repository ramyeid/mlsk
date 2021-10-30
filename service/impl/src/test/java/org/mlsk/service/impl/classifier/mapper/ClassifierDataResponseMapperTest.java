package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierDataResponseModel;
import org.mlsk.service.model.classifier.ClassifierDataResponse;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierDataResponseMapper.toClassifierDataResponseModel;

public class ClassifierDataResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_data_response_model() {
    ClassifierDataResponse classifierDataResponse = buildClassifierDataResponse();

    ClassifierDataResponseModel actualDataResponseModel = toClassifierDataResponseModel(classifierDataResponse);

    assertEquals(buildExpectedDataResponseModel(), actualDataResponseModel);
  }

  private static ClassifierDataResponse buildClassifierDataResponse() {
    return new ClassifierDataResponse("columnName", newArrayList(1, 2, 3));
  }

  private static ClassifierDataResponseModel buildExpectedDataResponseModel() {
    ClassifierDataResponseModel classifierDataResponseModel = new ClassifierDataResponseModel();
    classifierDataResponseModel.setColumnName("columnName");
    classifierDataResponseModel.setValues(newArrayList(1, 2, 3));
    return classifierDataResponseModel;
  }
}