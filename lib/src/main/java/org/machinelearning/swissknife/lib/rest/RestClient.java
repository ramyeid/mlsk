package org.machinelearning.swissknife.lib.rest;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

public class RestClient {

    private final ServiceInformation serviceInformation;
    private final RestTemplate restTemplate;

    public RestClient(ServiceInformation serviceInformation) {
        this(serviceInformation, new RestTemplate());
    }

    @VisibleForTesting
    RestClient(ServiceInformation serviceInformation, RestTemplate restTemplate) {
        this.serviceInformation = serviceInformation;
        this.restTemplate = restTemplate;
        MappingJackson2HttpMessageConverter messageConvereter = new MappingJackson2HttpMessageConverter();
        messageConvereter.setSupportedMediaTypes(singletonList(MediaType.ALL));
        this.restTemplate.setMessageConverters(singletonList(messageConvereter));
    }

    public <Body, Response> Response post(String endPoint, Body body, Class<Response> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Body> entity = new HttpEntity<Body>(body ,headers);
        return restTemplate.postForObject(serviceInformation.getUrl() + endPoint, entity, responseType);
    }
}
