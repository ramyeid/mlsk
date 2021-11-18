package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.*;

public interface ClassifierService {

  ClassifierStartResponse start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType);

  void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType);

  ClassifierDataResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType);

  Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType);
}