package org.mlsk.service.impl.classifier.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.engine.classifier.client.ClassifierEngineApi;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.impl.classifier.engine.mapper.*;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.model.classifier.*;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;

public class ClassifierEngineClient implements ClassifierEngine {

  private final ClassifierEngineApi classifierEngineApi;

  public ClassifierEngineClient(Endpoint endpoint, EngineClientFactory engineClientFactory) {
    this(engineClientFactory.buildClassifierClient(endpoint));
  }

  @VisibleForTesting
  ClassifierEngineClient(ClassifierEngineApi classifierEngineApi) {
    this.classifierEngineApi = classifierEngineApi;
  }

  @Override
  public void start(ClassifierStartRequest classifierStartRequest) {
    try {
      this.classifierEngineApi.start(ClassifierStartRequestMapper.toEngineModel(classifierStartRequest));
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "start");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "start");
    }
  }

  @Override
  public void data(ClassifierDataRequest classifierDataRequest) {
    try {
      this.classifierEngineApi.data(ClassifierDataRequestMapper.toEngineModel(classifierDataRequest));
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    }
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest) {
    try {
      return ClassifierResponseMapper.fromEngineModel(this.classifierEngineApi.predict(ClassifierRequestMapper.toEngineModel(classifierRequest)));
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest) {
    try {
      return this.classifierEngineApi.computePredictAccuracy(ClassifierRequestMapper.toEngineModel(classifierRequest)).doubleValue();
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    }
  }

  // TODO Make this method generic for all requests not classifiers only
  @Override
  public void cancel(ClassifierCancelRequest classifierCancelRequest) {
    try {
      this.classifierEngineApi.cancel(ClassifierCancelRequestMapper.toEngineModel(classifierCancelRequest));
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "cancel");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "cancel");
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
