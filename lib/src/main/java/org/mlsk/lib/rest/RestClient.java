package org.mlsk.lib.rest;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.exception.RestClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class RestClient {

  private final ServiceInformation serviceInformation;
  private final RestTemplate restTemplate;

  public RestClient(ServiceInformation serviceInformation) {
    this(serviceInformation, new RestTemplate());
  }

  @VisibleForTesting
  public RestClient(ServiceInformation serviceInformation, RestTemplate restTemplate) {
    this.serviceInformation = serviceInformation;
    this.restTemplate = restTemplate;
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setSupportedMediaTypes(newArrayList(MediaType.ALL));
    this.restTemplate.setMessageConverters(newArrayList(messageConverter));
  }

  public <Body, Response> Response post(String endPoint, @Nullable Body body, Class<Response> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Body> entity = new HttpEntity<>(body, headers);
    return restTemplate.postForObject(serviceInformation.getUrl() + endPoint, entity, responseType);
  }

  public <Response> Response post(String endPoint, Class<Response> responseType) {
    return post(endPoint, null, responseType);
  }

  // Some endpoints do not have a response, but a response is required by Flask.
  // In this case we do not need the return value.
  public <Body> Void post(String endPoint, @Nullable Body body) {
    Object response = post(endPoint, body, Object.class);
    throwExceptionIfNotDefaultResponse(response);
    return null;
  }

  // Some endpoints do not have a response, but a response is required by Flask.
  // In this case we do not need the return value.
  public Void post(String endPoint) {
    Object response = post(endPoint, Object.class);
    throwExceptionIfNotDefaultResponse(response);
    return null;
  }

  // Validating the contract between service and engine.
  private void throwExceptionIfNotDefaultResponse(Object response) {
    boolean isDefaultResponse = (response instanceof Map) && (((Map<String, String>) response).size() == 1) && (((Map<String, String>) response).get("Status").equals("Ok"));
    if (!isDefaultResponse) {
      throw new RestClientException("Contract between Engine and Service was breached, response different than 'Ok'");
    }
  }
}
