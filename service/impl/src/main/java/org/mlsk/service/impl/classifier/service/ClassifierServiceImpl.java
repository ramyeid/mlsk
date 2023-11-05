package org.mlsk.service.impl.classifier.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.service.exception.ClassifierServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.model.classifier.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;

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
    long requestId = RequestIdGenerator.nextId();
    try {
      LOGGER.info("[Start][{}] Start Request - Book Engine", requestId);
      orchestrator.bookEngine(requestId, classifierType.getStartAction());
      orchestrator.runOnEngine(requestId, classifierType.getStartAction(), engine -> engine.start(classifierStartRequest, classifierType));
      return new ClassifierStartResponse(valueOf(requestId));
    } catch (Exception exception) {
      cancelRequestAndReleaseEngine(requestId, classifierType.getStartAction(), classifierType);
      throw logAndBuildException(exception, requestId, "starting request");
    } finally {
      LOGGER.info("[End][{}] Start Request - Book Engine", requestId);
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start][{}] Data Request - Sending data to Engine", classifierDataRequest.getRequestId());
      orchestrator.runOnEngine(parseLong(classifierDataRequest.getRequestId()), classifierType.getDataAction(), engine -> engine.data(classifierDataRequest, classifierType));
    } catch (Exception exception) {
      cancelRequestAndReleaseEngine(parseLong(classifierDataRequest.getRequestId()), classifierType.getDataAction(), classifierType);
      throw logAndBuildException(exception, parseLong(classifierDataRequest.getRequestId()), "sending data to engine");
    } finally {
      LOGGER.info("[End][{}] Data Request - Sending data to Engine", classifierDataRequest.getRequestId());
    }
  }

  @Override
  public ClassifierDataResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start][{}] Predict Request", classifierRequest.getRequestId());
      return orchestrator.runOnEngine(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAction(), engine -> engine.predict(classifierType));
    } catch (Exception exception) {
      cancelRequest(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAction(), classifierType);
      throw logAndBuildException(exception, parseLong(classifierRequest.getRequestId()), "predicting on engine");
    } finally {
      releaseEngine(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAction());
      LOGGER.info("[End][{}] Predict Request", classifierRequest.getRequestId());
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      LOGGER.info("[Start][{}] Compute Predict Accuracy Request", classifierRequest.getRequestId());
      return orchestrator.runOnEngine(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAccuracyAction(), engine -> engine.computePredictAccuracy(classifierType));
    } catch (Exception exception) {
      cancelRequest(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAccuracyAction(), classifierType);
      throw logAndBuildException(exception, parseLong(classifierRequest.getRequestId()), "compute predict accuracy on engine");
    } finally {
      releaseEngine(parseLong(classifierRequest.getRequestId()), classifierType.getPredictAccuracyAction());
      LOGGER.info("[End][{}] Compute Predict Accuracy Request", classifierRequest.getRequestId());
    }
  }

  private void cancelRequestAndReleaseEngine(long requestId, String actionName, ClassifierType classifierType) {
    cancelRequest(requestId, actionName, classifierType);

    releaseEngine(requestId, actionName);
  }

  private void cancelRequest(long requestId, String actionName, ClassifierType classifierType) {
    catchExceptionAndLog(() -> {
      LOGGER.info("[{}] Cancelling request on engine with action {}", requestId, actionName);
      orchestrator.runOnEngine(requestId, classifierType.getCancelAction(), engine -> engine.cancel(classifierType));
    }, requestId);
  }

  private void releaseEngine(long requestId, String actionName) {
    catchExceptionAndLog(() -> {
      LOGGER.info("[{}] Releasing engine from action {}", requestId, actionName);
      orchestrator.completeRequest(requestId, actionName);
    }, requestId);
  }

  private static void catchExceptionAndLog(Runnable runnable, long requestId) {
    try {
      runnable.run();
    } catch (Exception exception) {
      LOGGER.info("[{}] Exception caught for request, {}", requestId, exception.getMessage());
    }
  }

  private static ClassifierServiceException logAndBuildException(Exception exception, Long requestId, String action) {
    LOGGER.error(format("[%d] Exception while %s: %s", requestId, action, exception.getMessage()), exception);
    return new ClassifierServiceException(exception.getMessage());
  }
}
