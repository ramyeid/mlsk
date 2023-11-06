package org.mlsk.service.classifier.resource.decisiontree;

import org.mlsk.service.classifier.resource.ClassifierResource;

import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.*;

public class DecisionTreeResource implements ClassifierResource {

  @Override
  public String getStartAction() {
    return DECISION_TREE_START;
  }

  @Override
  public String getDataAction() {
    return DECISION_TREE_DATA;
  }

  @Override
  public String getPredictAction() {
    return DECISION_TREE_PREDICT;
  }

  @Override
  public String getPredictAccuracyAction() {
    return DECISION_TREE_PREDICT_ACCURACY;
  }

  @Override
  public String getCancelAction() {
    return DECISION_TREE_CANCEL;
  }
}
