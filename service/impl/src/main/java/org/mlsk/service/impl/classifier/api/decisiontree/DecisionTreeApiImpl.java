package org.mlsk.service.impl.classifier.api.decisiontree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.service.classifier.decisiontree.api.DecisionTreeApi;
import org.mlsk.api.service.classifier.model.*;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.api.mapper.*;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.model.classifier.ClassifierResponse;
import org.mlsk.service.model.classifier.ClassifierStartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.mlsk.service.classifier.ClassifierType.DECISION_TREE;

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
    long requestId = RequestIdGenerator.nextId();
    LOGGER.info("[{}] Start request received", requestId);
    ClassifierStartResponse startResponse = service.start(ClassifierStartRequestMapper.fromServiceModel(requestId, classifierStartRequestModel), classifierType);
    return ResponseEntity.ok(ClassifierStartResponseMapper.toServiceModel(startResponse));
  }

  @Override
  public ResponseEntity<Void> data(ClassifierDataRequestModel classifierDataRequestModel) {
    LOGGER.info("[{}] Data request received", classifierDataRequestModel.getRequestId());
    service.data(ClassifierDataRequestMapper.fromServiceModel(classifierDataRequestModel), classifierType);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ClassifierResponseModel> predict(ClassifierRequestModel classifierRequestModel) {
    LOGGER.info("[{}] Predict request received", classifierRequestModel.getRequestId());
    ClassifierResponse response = service.predict(ClassifierRequestMapper.fromServiceModel(classifierRequestModel), classifierType);
    return ResponseEntity.ok(ClassifierResponseMapper.toServiceModel(response));
  }

  @Override
  public ResponseEntity<BigDecimal> computePredictAccuracy(ClassifierRequestModel classifierRequestModel) {
    LOGGER.info("[{}] Compute predict Accuracy request received", classifierRequestModel.getRequestId());
    Double accuracy = service.computePredictAccuracy(ClassifierRequestMapper.fromServiceModel(classifierRequestModel), classifierType);
    return ResponseEntity.ok(valueOf(accuracy));
  }
}
