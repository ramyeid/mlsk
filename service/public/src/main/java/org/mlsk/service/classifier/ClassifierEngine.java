package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.*;

public interface ClassifierEngine {

  void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType);

  void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType);

  ClassifierResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType);

  Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType);

  void cancel(ClassifierCancelRequest classifierCancelRequest, ClassifierType classifierType);
}