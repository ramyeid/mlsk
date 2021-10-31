package org.mlsk.service.classifier.resource.decisiontree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionTreeResourceTest {

  private DecisionTreeResource decisionTreeResource;

  @BeforeEach
  public void setUp() {
    this.decisionTreeResource = new DecisionTreeResource();
  }

  @Test
  public void should_return_correct_start_url() {

    String actual = decisionTreeResource.getStartUrl();

    assertEquals("/decision-tree/start", actual);
  }

  @Test
  public void should_return_correct_data_url() {

    String actual = decisionTreeResource.getDataUrl();

    assertEquals("/decision-tree/data", actual);
  }

  @Test
  public void should_return_correct_predict_url() {

    String actual = decisionTreeResource.getPredictUrl();

    assertEquals("/decision-tree/predict", actual);
  }

  @Test
  public void should_return_correct_predict_accuracy_url() {

    String actual = decisionTreeResource.getPredictAccuracyUrl();

    assertEquals("/decision-tree/predict-accuracy", actual);
  }

  @Test
  public void should_return_correct_cancel_url() {

    String actual = decisionTreeResource.getCancelUrl();

    assertEquals("/decision-tree/cancel", actual);
  }

  @Test
  public void should_return_correct_start_action() {

    String actual = decisionTreeResource.getStartAction();

    assertEquals("decision-tree-start", actual);
  }

  @Test
  public void should_return_correct_data_action() {

    String actual = decisionTreeResource.getDataAction();

    assertEquals("decision-tree-data", actual);
  }

  @Test
  public void should_return_correct_predict_action() {

    String actual = decisionTreeResource.getPredictAction();

    assertEquals("decision-tree-predict", actual);
  }

  @Test
  public void should_return_correct_predict_accuracy_action() {

    String actual = decisionTreeResource.getPredictAccuracyAction();

    assertEquals("decision-tree-compute-predict-accuracy", actual);
  }


  @Test
  public void should_return_correct_cancel_action() {

    String actual = decisionTreeResource.getCancelAction();

    assertEquals("decision-tree-cancel", actual);
  }
}