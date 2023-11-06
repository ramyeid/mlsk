package org.mlsk.lib.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mlsk.lib.rest.RestTemplateFactory.buildRestTemplate;

public class RestTemplateFactoryTest {

  @Test
  public void should_correctly_build_rest_template() {

    RestTemplate restTemplate = buildRestTemplate();

    List<HttpMessageConverter<?>> actualMessageConverters = restTemplate.getMessageConverters();
    assertEquals(1, actualMessageConverters.size());
    assertInstanceOf(MappingJackson2HttpMessageConverter.class, actualMessageConverters.get(0));
    assertEquals(1, actualMessageConverters.get(0).getSupportedMediaTypes().size());
    assertEquals(MediaType.ALL, actualMessageConverters.get(0).getSupportedMediaTypes().get(0));
  }
}