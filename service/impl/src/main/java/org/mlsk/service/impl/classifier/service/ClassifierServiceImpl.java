package org.mlsk.service.impl.classifier.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.model.classifier.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

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
    Optional<String> requestIdOptional = empty();
    try {
      LOGGER.info("[Start] Start Request - Book Engine");
      requestIdOptional = of(orchestrator.bookEngine(classifierType.getStartAction()));
      orchestrator.runOnEngine(requestIdOptional.get(), engine -> engine.start(classifierStartRequest, classifierType), classifierType.getStartAction());
      return new ClassifierStartResponse(requestIdOptional.get());
    } catch (Exception exception) {
      requestIdOptional.ifPresent(requestId -> cancelRequestAndReleaseEngine(requestId, classifierType.getStartAction(), classifierType));
      throw logAndBuildException(exception, "starting request");
    } finally {
      LOGGER.info("[End] Start Request - Book Engine");
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Data Request - Sending data to Engine");
      orchestrator.runOnEngine(classifierDataRequest.getRequestId(), engine -> engine.data(classifierDataRequest, classifierType), classifierType.getDataAction());
    } catch (Exception exception) {
      cancelRequestAndReleaseEngine(classifierDataRequest.getRequestId(), classifierType.getDataAction(), classifierType);
      throw logAndBuildException(exception, "sending data to engine");
    } finally {
      LOGGER.info("[End] Data Request - Sending data to Engine");
    }
  }

  @Override
  public ClassifierDataResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Predict Request");
      return orchestrator.runOnEngine(classifierRequest.getRequestId(), engine -> engine.predict(classifierType), classifierType.getPredictAction());
    } catch (Exception exception) {
      cancelRequest(classifierRequest.getRequestId(), classifierType.getPredictAction(), classifierType);
      throw logAndBuildException(exception, "predicting on engine");
    } finally {
      releaseEngine(classifierRequest.getRequestId(), classifierType.getPredictAction());
      LOGGER.info("[End] Predict Request");
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start] Compute Predict Accuracy Request");
      return orchestrator.runOnEngine(classifierRequest.getRequestId(), engine -> engine.computePredictAccuracy(classifierType), classifierType.getPredictAccuracyAction());
    } catch (Exception exception) {
      cancelRequest(classifierRequest.getRequestId(), classifierType.getPredictAccuracyAction(), classifierType);
      throw logAndBuildException(exception, "compute predict accuracy on engine");
    } finally {
      releaseEngine(classifierRequest.getRequestId(), classifierType.getPredictAccuracyAction());
      LOGGER.info("[End] Compute Predict Accuracy Request");
    }
  }

  private void cancelRequestAndReleaseEngine(String requestId, String actionName, ClassifierType classifierType) {
    cancelRequest(requestId, actionName, classifierType);

    releaseEngine(requestId, actionName);
  }

  private void cancelRequest(String requestId, String actionName, ClassifierType classifierType) {
    LOGGER.info("Cancelling request on engine with id {} and action {}", requestId, actionName);
    orchestrator.runOnEngine(requestId, engine -> engine.cancel(classifierType), classifierType.getCancelAction());
  }

  private void releaseEngine(String requestId, String actionName) {
    LOGGER.info("Releasing engine with request {} and action {}", requestId, actionName);
    orchestrator.releaseEngine(requestId, actionName);
  }

  private static ClassifierServiceException logAndBuildException(Exception exception, String action) {
    LOGGER.error(format("Exception while %s: %s", action, exception.getMessage()), exception);
    return new ClassifierServiceException(exception.getMessage());
  }
}
