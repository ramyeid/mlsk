package org.mlsk.service.impl.engine.client;

import org.mlsk.api.engine.classifier.decisiontree.client.DecisionTreeEngineApi;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.lib.model.Endpoint;
import org.springframework.web.client.RestTemplate;

import static com.google.common.collect.Lists.newArrayList;
import static org.mlsk.lib.rest.RestTemplateFactory.buildRestTemplate;

public class EngineClientFactory {

  public TimeSeriesAnalysisEngineApi buildTimeSeriesAnalysisClient(Endpoint endpoint) {
    RestTemplate restTemplate = buildRestTemplate();
    org.mlsk.api.engine.timeseries.client.ApiClient apiClient = new org.mlsk.api.engine.timeseries.client.ApiClient(restTemplate);
    apiClient.setBasePath(endpoint.getUrl());

    return new TimeSeriesAnalysisEngineApi(apiClient);
  }

  public DecisionTreeEngineApi buildDecisionTreeEngineApi(Endpoint endpoint) {
    RestTemplate restTemplate = buildRestTemplate();
    org.mlsk.api.engine.classifier.decisiontree.client.ApiClient apiClient = new org.mlsk.api.engine.classifier.decisiontree.client.ApiClient(restTemplate);
    apiClient.setBasePath(endpoint.getUrl());

    return new DecisionTreeEngineApi(apiClient);
  }
}
