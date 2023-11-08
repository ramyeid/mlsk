package org.mlsk.service.impl.classifier.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.engine.classifier.decisiontree.client.DecisionTreeEngineApi;
import org.mlsk.api.engine.classifier.model.ClassifierCancelRequestModel;
import org.mlsk.api.engine.classifier.model.ClassifierDataRequestModel;
import org.mlsk.api.engine.classifier.model.ClassifierRequestModel;
import org.mlsk.api.engine.classifier.model.ClassifierStartRequestModel;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.*;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierCancelRequestMapper.toClassifierCancelRequestModel;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierDataRequestMapper.toClassifierDataRequestModel;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierRequestMapper.toClassifierRequestModel;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierResponseMapper.toClassifierResponse;
import static org.mlsk.service.impl.classifier.engine.mapper.ClassifierStartRequestMapper.toClassifierStartRequestModel;

//TODO unify all classifier engine api
// - Create one ClassifierEngineApi instead of one for each (example: DecisionTreeEngineApi)
// - Pass in the classifier type to know what to do on the client side
// - This will enable us to remove the switch clause for each call.
public class ClassifierEngineClient implements ClassifierEngine {

  private final DecisionTreeEngineApi decisionTreeEngineApi;

  public ClassifierEngineClient(Endpoint endpoint, EngineClientFactory engineClientFactory) {
    this(engineClientFactory.buildDecisionTreeEngineApi(endpoint));
  }

  @VisibleForTesting
  ClassifierEngineClient(DecisionTreeEngineApi decisionTreeEngineApi) {
    this.decisionTreeEngineApi = decisionTreeEngineApi;
  }

  @Override
  public void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType) {
    try {
      callClassifierStart(classifierStartRequest, classifierType);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "start");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "start");
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      callClassifierData(classifierDataRequest, classifierType);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    }
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      return callClassifierPredict(classifierRequest, classifierType);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      return callClassifierComputePredictAccuracy(classifierRequest, classifierType);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    }
  }

  // TODO Make this method generic for all requests not classifiers only
  @Override
  public void cancel(ClassifierCancelRequest classifierCancelRequest, ClassifierType classifierType) {
    try {
      callClassifierCancel(classifierCancelRequest, classifierType);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "cancel");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "cancel");
    }
  }

  private void callClassifierStart(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType) {
    ClassifierStartRequestModel classifierStartRequestModel = toClassifierStartRequestModel(classifierStartRequest);

    switch (classifierType) {
      case DECISION_TREE:
        this.decisionTreeEngineApi.start(classifierStartRequestModel);
        break;
      default:
        throw new RuntimeException("Unknown Classifier Type");
    }
  }

  private void callClassifierData(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    ClassifierDataRequestModel classifierDataRequestModel = toClassifierDataRequestModel(classifierDataRequest);

    switch (classifierType) {
      case DECISION_TREE:
        this.decisionTreeEngineApi.data(classifierDataRequestModel);
        break;
      default:
        throw new RuntimeException("Unknown Classifier Type");
    }
  }

  private ClassifierResponse callClassifierPredict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    ClassifierRequestModel classifierRequestModel = toClassifierRequestModel(classifierRequest);

    switch (classifierType) {
      case DECISION_TREE:
        return toClassifierResponse(this.decisionTreeEngineApi.predict(classifierRequestModel));
      default:
        throw new RuntimeException("Unknown Classifier Type");
    }
  }

  private Double callClassifierComputePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    ClassifierRequestModel classifierRequestModel = toClassifierRequestModel(classifierRequest);

    switch (classifierType) {
      case DECISION_TREE:
        return this.decisionTreeEngineApi.computePredictAccuracy(classifierRequestModel).doubleValue();
      default:
        throw new RuntimeException("Unknown Classifier Type");
    }
  }

  private void callClassifierCancel(ClassifierCancelRequest classifierCancelRequest, ClassifierType classifierType) {
    ClassifierCancelRequestModel classifierCancelRequestModel = toClassifierCancelRequestModel(classifierCancelRequest);

    switch (classifierType) {
      case DECISION_TREE:
        this.decisionTreeEngineApi.cancel(classifierCancelRequestModel);
        break;
      default:
        throw new RuntimeException("Unknown Classifier Type");
    }
  }

  private static ClassifierEngineRequestException buildClassifierEngineRequestException(Exception exception, String action) {
    return new ClassifierEngineRequestException(format("Failed to post %s to engine", action), exception);
  }

  private static ClassifierEngineRequestException buildClassifierEngineRequestException(HttpServerErrorException exception, String action) {
    String message = format("Failed on post %s to engine: %s", action, exception.getResponseBodyAsString());
    return new ClassifierEngineRequestException(message, exception);
  }
}
