package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierDataRequestMapper.toClassifierDataRequest;

public class ClassifierDataRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_data_request() {
    ClassifierDataRequestModel classifierDataRequestModel = buildClassifierDataRequestModel();

    ClassifierDataRequest actualDataRequest = toClassifierDataRequest(classifierDataRequestModel);

    assertEquals(buildExpectedDataRequest(), actualDataRequest);
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel() {
    ClassifierDataRequestModel classifierDataRequestModel = new ClassifierDataRequestModel();
    classifierDataRequestModel.setRequestId("10");
    classifierDataRequestModel.setColumnName("columnName");
    classifierDataRequestModel.setValues(newArrayList(1, 81, 123));
    return classifierDataRequestModel;
  }

  private static ClassifierDataRequest buildExpectedDataRequest() {
    return new ClassifierDataRequest(10L, "columnName", newArrayList(1, 81, 123));
  }
}