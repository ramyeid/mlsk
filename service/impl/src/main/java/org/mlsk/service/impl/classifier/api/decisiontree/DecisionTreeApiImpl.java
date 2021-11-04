package org.mlsk.service.impl.classifier.api.decisiontree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.classifier.model.*;
import org.mlsk.api.decisiontree.api.DecisionTreeApi;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.classifier.ClassifierType.DECISION_TREE;
import static org.mlsk.service.impl.classifier.mapper.ClassifierDataRequestMapper.toClassifierDataRequest;
import static org.mlsk.service.impl.classifier.mapper.ClassifierDataResponseMapper.toClassifierDataResponseModel;
import static org.mlsk.service.impl.classifier.mapper.ClassifierRequestMapper.toClassifierRequest;
import static org.mlsk.service.impl.classifier.mapper.ClassifierStartRequestMapper.toClassifierStartRequest;
import static org.mlsk.service.impl.classifier.mapper.ClassifierStartResponseMapper.toClassifierStartResponseModel;

@RestController
public class DecisionTreeApiImpl implements DecisionTreeApi {

  private static final Logger LOGGER = LogManager.getLogger(DecisionTreeApiImpl.class);

  private final ClassifierService service;
  private final ClassifierType classifierType;

  @Autowired
  public DecisionTreeApiImpl(ClassifierService service) {
    this.service = service;
    this.classifierType = DECISION_TREE;
  }

  @Override
  public ResponseEntity<ClassifierStartResponseModel> start(ClassifierStartRequestModel classifierStartRequestModel) {
    LOGGER.info("Start request received");
    ClassifierStartResponse startResponse = service.start(toClassifierStartRequest(classifierStartRequestModel), classifierType);
    return ResponseEntity.ok(toClassifierStartResponseModel(startResponse));
  }

  @Override
  public ResponseEntity<Void> data(ClassifierDataRequestModel classifierDataRequestModel) {
    LOGGER.info("Data request received with request id {}", classifierDataRequestModel.getRequestId());
    service.data(toClassifierDataRequest(classifierDataRequestModel), classifierType);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ClassifierDataResponseModel> predict(ClassifierRequestModel classifierRequestModel) {
    LOGGER.info("Predict request received");
    ClassifierDataResponse dataResponse = service.predict(toClassifierRequest(classifierRequestModel), classifierType);
    return ResponseEntity.ok(toClassifierDataResponseModel(dataResponse));
  }

  @Override
  public ResponseEntity<BigDecimal> computePredictAccuracy(ClassifierRequestModel classifierRequestModel) {
    LOGGER.info("Compute predict Accuracy request received");
    Double accuracy = service.computePredictAccuracy(toClassifierRequest(classifierRequestModel), classifierType);
    return ResponseEntity.ok(valueOf(accuracy));
  }
}