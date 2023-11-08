package org.mlsk.service.impl.classifier.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.classifier.ClassifierEngine;
import org.mlsk.service.classifier.ClassifierType;
import org.mlsk.service.impl.classifier.engine.exception.ClassifierEngineRequestException;
import org.mlsk.service.model.classifier.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

// TODO Use OpenAPI models and Generate Python API from OpenAPI
public class ClassifierEngineClient implements ClassifierEngine {

  private final RestClient restClient;

  public ClassifierEngineClient(Endpoint endpoint) {
    this(new RestClient(endpoint));
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setSupportedMediaTypes(newArrayList(MediaType.ALL));
    restTemplate.setMessageConverters(newArrayList(messageConverter));
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
      throw buildClassifierEngineRequestException(exception, "start");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "start");
    }
  }

  @Override
  public Void data(ClassifierDataRequest classifierDataRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getDataUrl(), classifierDataRequest);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "data");
    }
  }

  @Override
  public ClassifierResponse predict(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getPredictUrl(), classifierRequest, ClassifierResponse.class);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict");
    }
  }

  @Override
  public Double computePredictAccuracy(ClassifierRequest classifierRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getPredictAccuracyUrl(), classifierRequest, Double.class);
    } catch (HttpServerErrorException exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    } catch (Exception exception) {
      throw buildClassifierEngineRequestException(exception, "predict accuracy");
    }
  }

  // TODO Make this method generic for all requests not classifiers only
  @Override
  public Void cancel(ClassifierCancelRequest classifierCancelRequest, ClassifierType classifierType) {
    try {
      return restClient.post(classifierType.getCancelUrl(), classifierCancelRequest);
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