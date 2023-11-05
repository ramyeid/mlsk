package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.*;

public interface ClassifierEngine {

  Void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType);

  Void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType);

  ClassifierResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType);

  Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType);

  Void cancel(ClassifierCancelRequest classifierCancelRequest, ClassifierType classifierType);
}