package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierResponseModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierResponseModel;
import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierStartResponseModel;

public class ClassifierModelHelperTest {

  @Test
  public void should_correctly_build_classifier_start_response_model() {
    long requestId = 10L;

    ClassifierStartResponseModel actualStartResponseModel = buildClassifierStartResponseModel(requestId);

    ClassifierStartResponseModel expectedStartResponseModel = new ClassifierStartResponseModel().requestId(requestId);
    assertEquals(expectedStartResponseModel, actualStartResponseModel);
  }

  @Test
  public void should_correctly_build_classifier_data_response_model() {
    String columnName = "columnName";
    List<Integer> values = newArrayList(1, 2, 3);

    ClassifierResponseModel actualResponseModel = buildClassifierResponseModel(columnName, values);

    ClassifierResponseModel expectedResponseModel = new ClassifierResponseModel().columnName(columnName).values(values);
    assertEquals(expectedResponseModel, actualResponseModel);
  }
}