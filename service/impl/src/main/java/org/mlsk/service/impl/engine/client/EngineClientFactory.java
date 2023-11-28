package org.mlsk.service.impl.engine.client;

import org.mlsk.api.engine.admin.client.AdminEngineApi;
import org.mlsk.api.engine.classifier.client.ClassifierEngineApi;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.lib.model.Endpoint;
import org.springframework.web.client.RestTemplate;

import static org.mlsk.lib.rest.RestTemplateFactory.buildRestTemplate;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineApi buildTimeSeriesAnalysisClient(Endpoint endpoint) {
    RestTemplate restTemplate = buildRestTemplate();
    org.mlsk.api.engine.timeseries.client.ApiClient apiClient = new org.mlsk.api.engine.timeseries.client.ApiClient(restTemplate);
    apiClient.setBasePath(endpoint.getUrl());

    return new TimeSeriesAnalysisEngineApi(apiClient);
  }

  public ClassifierEngineApi buildClassifierClient(Endpoint endpoint) {
    RestTemplate restTemplate = buildRestTemplate();
    org.mlsk.api.engine.classifier.client.ApiClient apiClient = new org.mlsk.api.engine.classifier.client.ApiClient(restTemplate);
    apiClient.setBasePath(endpoint.getUrl());

    return new ClassifierEngineApi(apiClient);
  }

  public AdminEngineApi buildAdminClient(Endpoint endpoint) {
    RestTemplate restTemplate = buildRestTemplate();
    org.mlsk.api.engine.admin.client.ApiClient apiClient = new org.mlsk.api.engine.admin.client.ApiClient(restTemplate);
    apiClient.setBasePath(endpoint.getUrl());

    return new AdminEngineApi(apiClient);
  }
}
