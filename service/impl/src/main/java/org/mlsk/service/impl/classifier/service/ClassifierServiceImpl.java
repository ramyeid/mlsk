package org.mlsk.service.impl.classifier.service;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.model.classifier.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ClassifierServiceImpl implements ClassifierService {

  private static final Logger LOGGER = LogManager.getLogger(ClassifierServiceImpl.class);

  private final Orchestrator orchestrator;

  @Autowired
  public ClassifierServiceImpl(Orchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @Override
  public ClassifierStartResponse start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Start Request - Book Engine");
      Pair<String, Void> requestIdAndResult = orchestrator.runOnEngineAndBlock(engine -> engine.start(classifierStartRequest, classifierType), classifierType.getStartAction());
      return new ClassifierStartResponse(requestIdAndResult.getLeft());
    } catch (Exception exception) {
      LOGGER.error(format("Exception while starting request: %s", exception.getMessage()), exception);
      throw new ClassifierServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] Start Request - Book Engine");
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Data Request - Sending data to Engine");
      orchestrator.runOnEngineAndBlock(classifierDataRequest.getRequestId(), engine -> engine.data(classifierDataRequest, classifierType), classifierType.getDataAction());
    } catch (Exception exception) {
      LOGGER.error(format("Exception while sending data to engine: %s", exception.getMessage()), exception);
      throw new ClassifierServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] Data Request - Sending data to Engine");
    }
  }

  @Override
  public ClassifierDataResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Predict Request");
      return orchestrator.runOnEngineAndUnblock(classifierRequest.getRequestId(), engine -> engine.predict(classifierType), classifierType.getPredictAction());
    } catch (Exception exception) {
      LOGGER.error(format("Exception while predicting on engine: %s", exception.getMessage()), exception);
      throw new ClassifierServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] Predict Request");
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Compute Predict Accuracy Request");
      return orchestrator.runOnEngineAndUnblock(classifierRequest.getRequestId(), engine -> engine.computePredictAccuracy(classifierType), classifierType.getPredictAccuracyAction());
    } catch (Exception exception) {
      LOGGER.error(format("Exception while compute predict accuracy on engine: %s", exception.getMessage()), exception);
      throw new ClassifierServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End] Compute Predict Accuracy Request");
    }
  }
}
