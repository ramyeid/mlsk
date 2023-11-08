package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierDataRequestMapper.toEngineModel;

public class ClassifierDataRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_data_request_model() {
    ClassifierDataRequest classifierDataRequest = buildDataRequest();

    ClassifierDataRequestModel actualDataRequestModel = toEngineModel(classifierDataRequest);

    assertEquals(buildExpectedModel(), actualDataRequestModel);
  }

  private static ClassifierDataRequestModel buildExpectedModel() {
    return new ClassifierDataRequestModel(10L, "columnName", newArrayList(1, 81, 123));
  }

  private static ClassifierDataRequest buildDataRequest() {
    return new ClassifierDataRequest(10L, "columnName", newArrayList(1, 81, 123));
  }
}