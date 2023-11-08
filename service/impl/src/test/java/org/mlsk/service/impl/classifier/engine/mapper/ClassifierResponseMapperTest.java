package org.mlsk.service.impl.classifier.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.classifier.model.ClassifierResponseModel;
import org.mlsk.api.engine.classifier.model.ClassifierTypeModel;
import org.mlsk.service.model.classifier.ClassifierResponse;
import org.mlsk.service.model.classifier.ClassifierType;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierResponseMapper.fromEngineModel;

public class ClassifierResponseMapperTest {

  @Test
  public void should_correctly_map_to_classifier_response_model() {
    ClassifierResponseModel classifierResponseModel = buildResponseModel();

    ClassifierResponse actualResponse = fromEngineModel(classifierResponseModel);

    assertEquals(buildExpectedResponse(), actualResponse);
  }

  private static ClassifierResponse buildExpectedResponse() {
    return new ClassifierResponse(1L, "columnName", newArrayList(1, 2, 3), ClassifierType.DECISION_TREE);
  }

  private static ClassifierResponseModel buildResponseModel() {
    return new ClassifierResponseModel(1L, "columnName", newArrayList(1, 2, 3), ClassifierTypeModel.DECISION_TREE);
  }
}