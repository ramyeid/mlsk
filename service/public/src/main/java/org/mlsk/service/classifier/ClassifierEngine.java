package org.mlsk.service.classifier;

import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;

public interface ClassifierEngine {

  Void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType);

  Void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType);

  ClassifierDataResponse predict(ClassifierType classifierType);

  Double computePredictAccuracy(ClassifierType classifierType);

  Void cancel(ClassifierType classifierType);
}