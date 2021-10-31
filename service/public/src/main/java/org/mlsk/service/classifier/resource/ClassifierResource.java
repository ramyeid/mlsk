package org.mlsk.service.classifier.resource;

public interface ClassifierResource {

  String getStartUrl();

  String getDataUrl();

  String getPredictUrl();

  String getPredictAccuracyUrl();

  String getCancelUrl();

  String getStartAction();

  String getDataAction();

  String getPredictAction();

  String getPredictAccuracyAction();

  String getCancelAction();
}
