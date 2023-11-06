package org.mlsk.service.classifier.resource;

public interface ClassifierResource {

  String getStartAction();

  String getDataAction();

  String getPredictAction();

  String getPredictAccuracyAction();

  String getCancelAction();
}
