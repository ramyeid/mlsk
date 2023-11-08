package org.mlsk.service.model.classifier;


import static org.mlsk.service.model.classifier.utils.DecisionTreeConstants.*;

public enum ClassifierType {

  DECISION_TREE(DECISION_TREE_START, DECISION_TREE_DATA, DECISION_TREE_PREDICT, DECISION_TREE_PREDICT_ACCURACY, DECISION_TREE_CANCEL);

  private final String startAction;
  private final String dataAction;
  private final String predictAction;
  private final String predictAccuracyAction;
  private final String cancelAction;

  ClassifierType(String startAction, String dataAction, String predictAction, String predictAccuracyAction, String cancelAction) {
    this.startAction = startAction;
    this.dataAction = dataAction;
    this.predictAction = predictAction;
    this.predictAccuracyAction = predictAccuracyAction;
    this.cancelAction = cancelAction;
  }

  public String getStartAction() {
    return this.startAction;
  }

  public String getDataAction() {
    return this.dataAction;
  }

  public String getPredictAction() {
    return this.predictAction;
  }

  public String getPredictAccuracyAction() {
    return predictAccuracyAction;
  }

  public String getCancelAction() {
    return cancelAction;
  }
}
