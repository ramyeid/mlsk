package org.machinelearning.swissknife.lib.rest;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.web.client.RestTemplate;

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
    }

    public <Body, Response> Response post(String endPoint, Body body, Class<Response> responseType) {
        return restTemplate.postForObject(serviceInformation.getUrl() + endPoint, body, responseType);
    }
}
