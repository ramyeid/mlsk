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
    long requestId = classifierStartRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] Start Request - Book Engine", requestId);
      orchestrator.bookEngine(requestId, classifierType.getStartAction());
      orchestrator.runOnEngine(requestId, classifierType.getStartAction(), engine -> {
        engine.start(classifierStartRequest, classifierType);
        return null;
      });
      return new ClassifierStartResponse(requestId);
    } catch (Exception exception) {
      cancelRequest(requestId, classifierType.getStartAction(), classifierType);
      releaseEngine(requestId, classifierType.getStartAction());
      throw logAndBuildException(exception, requestId, "starting request");
    } finally {
      LOGGER.info("[End][{}] Start Request - Book Engine", requestId);
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    long requestId = classifierDataRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] Data Request - Sending data to Engine", requestId);
      orchestrator.runOnEngine(requestId, classifierType.getDataAction(), engine -> {
        engine.data(classifierDataRequest, classifierType);
        return null;
      });
    } catch (Exception exception) {
      cancelRequest(requestId, classifierType.getDataAction(), classifierType);
      releaseEngine(requestId, classifierType.getDataAction());
      throw logAndBuildException(exception, requestId, "sending data to engine");
    } finally {
      LOGGER.info("[End][{}] Data Request - Sending data to Engine", requestId);
    }
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    long requestId = classifierRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] Predict Request", requestId);
      return orchestrator.runOnEngine(requestId, classifierType.getPredictAction(), engine -> engine.predict(classifierRequest, classifierType));
    } catch (Exception exception) {
      cancelRequest(requestId, classifierType.getPredictAction(), classifierType);
      throw logAndBuildException(exception, requestId, "predicting on engine");
    } finally {
      releaseEngine(requestId, classifierType.getPredictAction());
      LOGGER.info("[End][{}] Predict Request", requestId);
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    long requestId = classifierRequest.getRequestId();
    try {
      LOGGER.info("[Start][{}] Compute Predict Accuracy Request", requestId);
      return orchestrator.runOnEngine(requestId, classifierType.getPredictAccuracyAction(), engine -> engine.computePredictAccuracy(classifierRequest, classifierType));
    } catch (Exception exception) {
      cancelRequest(requestId, classifierType.getPredictAccuracyAction(), classifierType);
      throw logAndBuildException(exception, requestId, "compute predict accuracy on engine");
    } finally {
      releaseEngine(requestId, classifierType.getPredictAccuracyAction());
      LOGGER.info("[End][{}] Compute Predict Accuracy Request", requestId);
    }
  }

  private void cancelRequest(long requestId, String actionName, ClassifierType classifierType) {
    catchExceptionAndLog(() -> {
      LOGGER.info("[{}] Cancelling request on engine with action {}", requestId, actionName);
      ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(requestId);
      orchestrator.runOnEngine(requestId, classifierType.getCancelAction(), engine -> {
        engine.cancel(classifierCancelRequest, classifierType);
        return null;
      });
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
