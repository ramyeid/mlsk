package org.mlsk.service.classifier;

import org.junit.jupiter.api.Test;
import org.mlsk.service.classifier.resource.decisiontree.DecisionTreeResource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierTypeTest {

  private static final DecisionTreeResource DECISION_TREE_RESOURCE = new DecisionTreeResource();

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