package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.*;

public interface ClassifierService {

  ClassifierStartResponse start(ClassifierStartRequest classifierStartRequest);

  void data(ClassifierDataRequest classifierDataRequest);

  ClassifierResponse predict(ClassifierRequest classifierRequest);

  Double computePredictAccuracy(ClassifierRequest classifierRequest);
}
