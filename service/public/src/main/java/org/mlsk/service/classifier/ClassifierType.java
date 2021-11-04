package org.mlsk.service.classifier;

import org.mlsk.service.classifier.resource.ClassifierResource;
import org.mlsk.service.classifier.resource.decisiontree.DecisionTreeResource;

public enum ClassifierType {

  DECISION_TREE(new DecisionTreeResource());

  private final ClassifierResource classifierResource;

  ClassifierType(ClassifierResource classifierResource) {
    this.classifierResource = classifierResource;
  }

  public String getStartUrl() {
    return classifierResource.getStartUrl();
  }

  public String getDataUrl() {
    return classifierResource.getDataUrl();
  }

  public String getPredictUrl() {
    return classifierResource.getPredictUrl();
  }

  public String getPredictAccuracyUrl() {
    return classifierResource.getPredictAccuracyUrl();
  }

  public String getCancelUrl() {
    return classifierResource.getCancelUrl();
  }

  public String getStartAction() {
    return classifierResource.getStartAction();
  }

  public String getDataAction() {
    return classifierResource.getDataAction();
  }

  public String getPredictAction() {
    return classifierResource.getPredictAction();
  }

  public String getPredictAccuracyAction() {
    return classifierResource.getPredictAccuracyAction();
  }

  public String getCancelAction() {
    return classifierResource.getCancelAction();
  }
}
