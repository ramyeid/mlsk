package org.mlsk.service.classifier.resource.decisiontree;

import org.mlsk.service.classifier.resource.ClassifierResource;

import static org.mlsk.service.classifier.resource.decisiontree.DecisionTreeConstants.*;

public class DecisionTreeResource implements ClassifierResource {

  @Override
  public String getStartUrl() {
    return START_URL;
  }

  @Override
  public String getDataUrl() {
    return DATA_URL;
  }

  @Override
  public String getPredictUrl() {
    return PREDICT_URL;
  }

  @Override
  public String getPredictAccuracyUrl() {
    return PREDICT_ACCURACY_URL;
  }

  @Override
  public String getCancelUrl() {
    return CANCEL_URL;
  }

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
