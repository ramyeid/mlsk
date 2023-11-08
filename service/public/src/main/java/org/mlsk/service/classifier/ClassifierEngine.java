package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.*;

public interface ClassifierEngine {

  void start(ClassifierStartRequest classifierStartRequest);

  void data(ClassifierDataRequest classifierDataRequest);

  ClassifierResponse predict(ClassifierRequest classifierRequest);

  Double computePredictAccuracy(ClassifierRequest classifierRequest);

  void cancel(ClassifierCancelRequest classifierCancelRequest);
}