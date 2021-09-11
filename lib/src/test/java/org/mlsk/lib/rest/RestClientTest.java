package org.mlsk.lib.rest;

import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

public class RestClientTest {

  private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation("hp123", 6767L);

  @Test
  public void should_delegate_post_call_to_rest_template() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    RestClient restClient = new RestClient(SERVICE_INFORMATION, restTemplate);
    reset(restTemplate);
    Double body = 123d;

    restClient.post("forecast", body, String.class);

    HttpHeaders expectedHeaders = new HttpHeaders();
    expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Double> expectedEntity = new HttpEntity<>(body, expectedHeaders);
    verify(restTemplate).postForObject("http://hp123:6767/forecast", expectedEntity, String.class);
  }
}