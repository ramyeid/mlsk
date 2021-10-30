package org.mlsk.service.impl.classifier.decisiontree.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.api.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.classifier.model.ClassifierDataResponseModel;
import org.mlsk.api.classifier.model.ClassifierStartRequestModel;
import org.mlsk.api.classifier.model.ClassifierStartResponseModel;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.google.common.collect.Lists.newArrayList;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;

public class DecisionTreeApiImplTest {

  private DecisionTreeApiImpl decisionTreeApi;

  @BeforeEach
  public void setUp() {
    this.decisionTreeApi = new DecisionTreeApiImpl();
  }

  @Test
  public void should_return_correct_response_on_start() {

    ResponseEntity<ClassifierStartResponseModel> actualResponse = decisionTreeApi.start(new ClassifierStartRequestModel());

    assertOnResponseEntity(new ClassifierStartResponseModel().requestId("id1"), actualResponse);
  }

  @Test
  public void should_return_correct_response_on_data() {

    ResponseEntity<Void> actualResponse = decisionTreeApi.data(new ClassifierDataRequestModel());

    assertOnResponseEntity(null, actualResponse);
  }

  @Test
  public void should_return_correct_response_on_predict() {

    ResponseEntity<ClassifierDataResponseModel> actualResponse = decisionTreeApi.predict();

    assertOnResponseEntity(new ClassifierDataResponseModel().columnName("col1").values(newArrayList(0, 1, 1)), actualResponse);
  }

  @Test
  public void should_return_correct_response_on_compute_predict_accuracy() {

    ResponseEntity<BigDecimal> actualResponse = decisionTreeApi.computePredictAccuracy();

    assertOnResponseEntity(BigDecimal.ONE, actualResponse);
  }
}