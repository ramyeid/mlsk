package org.mlsk.lib.rest;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.google.common.collect.Lists.newArrayList;

public final class RestTemplateFactory {

  private RestTemplateFactory() {
  }

  public static RestTemplate buildRestTemplate() {
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setSupportedMediaTypes(newArrayList(MediaType.ALL));

    return new RestTemplate(newArrayList(messageConverter));
  }
}
