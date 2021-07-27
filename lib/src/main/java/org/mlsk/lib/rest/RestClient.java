package org.mlsk.lib.rest;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.google.common.collect.Lists.newArrayList;

public class RestClient {

  private final IServiceInformation serviceInformation;
  private final RestTemplate restTemplate;

  public RestClient(IServiceInformation serviceInformation) {
    this(serviceInformation, new RestTemplate());
  }

  @VisibleForTesting
  public RestClient(IServiceInformation serviceInformation, RestTemplate restTemplate) {
    this.serviceInformation = serviceInformation;
    this.restTemplate = restTemplate;
    MappingJackson2HttpMessageConverter messageConvereter = new MappingJackson2HttpMessageConverter();
    messageConvereter.setSupportedMediaTypes(newArrayList(MediaType.ALL));
    this.restTemplate.setMessageConverters(newArrayList(messageConvereter));
  }

  public <Body, Response> Response post(String endPoint, Body body, Class<Response> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Body> entity = new HttpEntity<>(body, headers);
    return restTemplate.postForObject(serviceInformation.getUrl() + endPoint, entity, responseType);
  }
}
