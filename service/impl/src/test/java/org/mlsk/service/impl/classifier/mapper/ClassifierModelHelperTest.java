package org.mlsk.service.impl.classifier.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierDataResponseModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierDataResponseModel;
import static org.mlsk.service.impl.classifier.mapper.ClassifierModelHelper.buildClassifierStartResponseModel;

public class ClassifierModelHelperTest {

  @Test
  public void should_correctly_build_classifier_start_response_model() {
    long requestId = 10L;

    ClassifierStartResponseModel actualStartResponseModel = buildClassifierStartResponseModel(requestId);

    ClassifierStartResponseModel expectedStartResponseModel = new ClassifierStartResponseModel().requestId(valueOf(requestId));
    assertEquals(expectedStartResponseModel, actualStartResponseModel);
  }

  @Test
  public void should_correctly_build_classifier_data_response_model() {
    String columnName = "columnName";
    List<Integer> values = newArrayList(1, 2, 3);

    ClassifierDataResponseModel actualDataResponseModel = buildClassifierDataResponseModel(columnName, values);

    ClassifierDataResponseModel expectedDataResponseModel = new ClassifierDataResponseModel().columnName(columnName).values(values);
    assertEquals(expectedDataResponseModel, actualDataResponseModel);
  }
}