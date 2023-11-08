package org.mlsk.service.impl.classifier.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.classifier.ClassifierService;
import org.mlsk.service.model.classifier.ClassifierType;
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
  public ClassifierStartResponse start(ClassifierStartRequest classifierStartRequest) {
    long requestId = classifierStartRequest.getRequestId();
    ClassifierType classifierType = classifierStartRequest.getClassifierType();
    String startAction = classifierType.getStartAction();

    try {
      LOGGER.info("[Start][{}] Start Request - Book Engine", requestId);
      orchestrator.bookEngine(requestId, startAction);
      orchestrator.runOnEngine(requestId, startAction, engine -> {
        engine.start(classifierStartRequest);
        return null;
      });
      return new ClassifierStartResponse(requestId, classifierType);
    } catch (Exception exception) {
      cancelRequest(requestId, startAction, classifierType);
      releaseEngine(requestId, startAction);
      throw logAndBuildException(exception, requestId, "starting request");
    } finally {
      LOGGER.info("[End][{}] Start Request - Book Engine", requestId);
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest) {
    long requestId = classifierDataRequest.getRequestId();
    ClassifierType classifierType = classifierDataRequest.getClassifierType();
    String dataAction = classifierType.getDataAction();

    try {
      LOGGER.info("[Start][{}] Data Request - Sending data to Engine", requestId);
      orchestrator.runOnEngine(requestId, dataAction, engine -> {
        engine.data(classifierDataRequest);
        return null;
      });
    } catch (Exception exception) {
      cancelRequest(requestId, dataAction, classifierType);
      releaseEngine(requestId, dataAction);
      throw logAndBuildException(exception, requestId, "sending data to engine");
    } finally {
      LOGGER.info("[End][{}] Data Request - Sending data to Engine", requestId);
    }
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest) {
    long requestId = classifierRequest.getRequestId();
    ClassifierType classifierType = classifierRequest.getClassifierType();
    String predictAction = classifierType.getPredictAction();

    try {
      LOGGER.info("[Start][{}] Predict Request", requestId);
      return orchestrator.runOnEngine(requestId, predictAction, engine -> engine.predict(classifierRequest));
    } catch (Exception exception) {
      cancelRequest(requestId, predictAction, classifierType);
      throw logAndBuildException(exception, requestId, "predicting on engine");
    } finally {
      releaseEngine(requestId, predictAction);
      LOGGER.info("[End][{}] Predict Request", requestId);
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest) {
    long requestId = classifierRequest.getRequestId();
    ClassifierType classifierType = classifierRequest.getClassifierType();
    String predictAccuracyAction = classifierType.getPredictAccuracyAction();

    try {
      LOGGER.info("[Start][{}] Compute Predict Accuracy Request", requestId);
      return orchestrator.runOnEngine(requestId, predictAccuracyAction, engine -> engine.computePredictAccuracy(classifierRequest));
    } catch (Exception exception) {
      cancelRequest(requestId, predictAccuracyAction, classifierType);
      throw logAndBuildException(exception, requestId, "compute predict accuracy on engine");
    } finally {
      releaseEngine(requestId, predictAccuracyAction);
      LOGGER.info("[End][{}] Compute Predict Accuracy Request", requestId);
    }
  }

  private void cancelRequest(long requestId, String actionName, ClassifierType classifierType) {
    catchExceptionAndLog(() -> {
      LOGGER.info("[{}] Cancelling request on engine with action {}", requestId, actionName);
      ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(requestId, classifierType);
      orchestrator.runOnEngine(requestId, classifierType.getCancelAction(), engine -> {
        engine.cancel(classifierCancelRequest);
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
