package org.mlsk.service.impl.classifier.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.ClassifierDataRequest;
import org.mlsk.service.model.classifier.ClassifierDataResponse;
import org.mlsk.service.model.classifier.ClassifierStartRequest;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;

public class ClassifierEngineClient implements ClassifierEngine {

  private final RestClient restClient;

  public ClassifierEngineClient(ServiceInformation serviceInformation) {
    this(new RestClient(serviceInformation));
  }

  @VisibleForTesting
  public ClassifierEngineClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Void start(ClassifierStartRequest classifierStartRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getStartUrl(), classifierStartRequest);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post start to engine: %s", exception.getResponseBodyAsString());
      throw new ClassifierEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new ClassifierEngineRequestException("Failed to post start to engine", exception);
    }
  }

  @Override
  public Void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getDataUrl(), classifierDataRequest);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post data to engine: %s", exception.getResponseBodyAsString());
      throw new ClassifierEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new ClassifierEngineRequestException("Failed to post data to engine", exception);
    }
  }

  @Override
  public ClassifierDataResponse predict(ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getPredictUrl(), ClassifierDataResponse.class);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post predict to engine: %s", exception.getResponseBodyAsString());
      throw new ClassifierEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new ClassifierEngineRequestException("Failed to post predict to engine", exception);
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getPredictAccuracyUrl(), Double.class);
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post predict accuracy to engine: %s", exception.getResponseBodyAsString());
      throw new ClassifierEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new ClassifierEngineRequestException("Failed to post predict accuracy to engine", exception);
    }
  }

  @Override
  public Void cancel(ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getCancelUrl());
    } catch (HttpServerErrorException exception) {
      String engineException = format("Failed on post cancel to engine: %s", exception.getResponseBodyAsString());
      throw new ClassifierEngineRequestException(engineException, exception);
    } catch (Exception exception) {
      throw new ClassifierEngineRequestException("Failed to post cancel to engine", exception);
    }
  }
}