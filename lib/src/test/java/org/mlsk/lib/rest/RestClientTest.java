package org.mlsk.lib.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.lib.rest.exception.RestClientException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestClientTest {

  private static final Endpoint ENDPOINT = new Endpoint("hp123", 6767L);

  @Mock
  private RestTemplate restTemplate;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = new RestClient(ENDPOINT, restTemplate);
    reset(restTemplate);
  }

  @Test
  public void should_delegate_post_call_to_rest_template_with_body_with_response() {
    Double body = 123d;

    restClient.post("forecast", body, BigDecimal.class);

    HttpHeaders expectedHeaders = new HttpHeaders();
    expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Double> expectedEntity = new HttpEntity<>(body, expectedHeaders);
    verify(restTemplate).postForObject("http://hp123:6767/forecast", expectedEntity, BigDecimal.class);
  }

  @Test
  public void should_delegate_post_call_to_rest_template_with_body_without_response() {
    Double body = 123d;
    onPostWithObjectReturn(buildDefaultResponse());

    restClient.post("forecast", body);

    HttpHeaders expectedHeaders = new HttpHeaders();
    expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Double> expectedEntity = new HttpEntity<>(body, expectedHeaders);
    verify(restTemplate).postForObject("http://hp123:6767/forecast", expectedEntity, Object.class);
  }

  @Test
  public void should_throw_exception_on_post_call_to_rest_template_with_body_without_response_and_response_not_ok() {
    Double body = 123d;
    onPostWithObjectReturn("NotOk");

    try {
      restClient.post("forecast", body);
      fail("should fail since response not ok");

    } catch (Exception exception) {
      assertOnRestClientException(exception);
    }
  }

  @Test
  public void should_delegate_post_call_to_rest_template_without_body_with_response() {

    restClient.post("forecast", BigDecimal.class);

    HttpHeaders expectedHeaders = new HttpHeaders();
    expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Double> expectedEntity = new HttpEntity<>(null, expectedHeaders);
    verify(restTemplate).postForObject("http://hp123:6767/forecast", expectedEntity, BigDecimal.class);
  }

  @Test
  public void should_delegate_post_call_to_rest_template_without_body_without_response() {
    onPostWithObjectReturn(buildDefaultResponse());

    restClient.post("forecast");

    HttpHeaders expectedHeaders = new HttpHeaders();
    expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Double> expectedEntity = new HttpEntity<>(null, expectedHeaders);
    verify(restTemplate).postForObject("http://hp123:6767/forecast", expectedEntity, Object.class);
  }

  @Test
  public void should_throw_exception_on_post_call_to_rest_template_without_body_without_response_and_response_not_ok() {
    onPostWithObjectReturn("NotOk");

    try {
      restClient.post("forecast");
      fail("should fail since response not ok");

    } catch (Exception exception) {
      assertOnRestClientException(exception);
    }
  }

  private void onPostWithObjectReturn(Object response) {
    when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), any(Class.class))).thenReturn(response);
  }

  private static Map<String, String> buildDefaultResponse() {
    HashMap<String, String> response = newHashMap();
    response.put("Status", "Ok");
    return response;
  }

  private static void assertOnRestClientException(Exception exception) {
    assertInstanceOf(RestClientException.class, exception);
    assertEquals("Contract between Engine and Service was breached, response different than 'Ok'", exception.getMessage());
  }
}