package org.mlsk.service.classifier;

import org.junit.jupiter.api.Test;
import org.mlsk.service.classifier.resource.decisiontree.DecisionTreeResource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierTypeTest {

  private static final DecisionTreeResource DECISION_TREE_RESOURCE = new DecisionTreeResource();

  @Test
  public void should_correctly_delegate_call_on_start_url() {

    String actual = ClassifierType.DECISION_TREE.getStartUrl();

    assertEquals(DECISION_TREE_RESOURCE.getStartUrl(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_data_url() {

    String actual = ClassifierType.DECISION_TREE.getDataUrl();

    assertEquals(DECISION_TREE_RESOURCE.getDataUrl(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_predict_url() {

    String actual = ClassifierType.DECISION_TREE.getPredictUrl();

    assertEquals(DECISION_TREE_RESOURCE.getPredictUrl(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_predict_accuracy_url() {

    String actual = ClassifierType.DECISION_TREE.getPredictAccuracyUrl();

    assertEquals(DECISION_TREE_RESOURCE.getPredictAccuracyUrl(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_cancel_url() {

    String actual = ClassifierType.DECISION_TREE.getCancelUrl();

    assertEquals(DECISION_TREE_RESOURCE.getCancelUrl(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_start_action() {

    String actual = ClassifierType.DECISION_TREE.getStartAction();

    assertEquals(DECISION_TREE_RESOURCE.getStartAction(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_data_action() {

    String actual = ClassifierType.DECISION_TREE.getDataAction();

    assertEquals(DECISION_TREE_RESOURCE.getDataAction(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_predict_action() {

    String actual = ClassifierType.DECISION_TREE.getPredictAction();

    assertEquals(DECISION_TREE_RESOURCE.getPredictAction(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_predict_accuracy_action() {

    String actual = ClassifierType.DECISION_TREE.getPredictAccuracyAction();

    assertEquals(DECISION_TREE_RESOURCE.getPredictAccuracyAction(), actual);
  }

  @Test
  public void should_correctly_delegate_call_on_cancel_action() {

    String actual = ClassifierType.DECISION_TREE.getCancelAction();

    assertEquals(DECISION_TREE_RESOURCE.getCancelAction(), actual);
  }
}