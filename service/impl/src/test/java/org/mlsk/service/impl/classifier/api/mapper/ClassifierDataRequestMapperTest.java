package org.mlsk.service.impl.classifier.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.classifier.model.ClassifierDataRequestModel;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierType;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.api.mapper.ClassifierDataRequestMapper.fromServiceModel;

public class ClassifierDataRequestMapperTest {

  @Test
  public void should_correctly_map_to_classifier_data_request() {
    ClassifierDataRequestModel classifierDataRequestModel = buildClassifierDataRequestModel();

    ClassifierDataRequest actualDataRequest = fromServiceModel(classifierDataRequestModel, ClassifierType.DECISION_TREE);

    assertEquals(buildExpectedDataRequest(), actualDataRequest);
  }

  private static ClassifierDataRequestModel buildClassifierDataRequestModel() {
    return new ClassifierDataRequestModel(10L, "columnName", newArrayList(1, 81, 123));
  }

  private static ClassifierDataRequest buildExpectedDataRequest() {
    return new ClassifierDataRequest(10L, "columnName", newArrayList(1, 81, 123), ClassifierType.DECISION_TREE);
  }
}