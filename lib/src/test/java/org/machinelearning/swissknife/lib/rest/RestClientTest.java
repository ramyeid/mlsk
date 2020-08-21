package org.machinelearning.swissknife.lib.rest;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RestClientTest {

    private static final String HOST = "hp123";
    private static final String PORT = "6767";
    private static final ServiceInformation SERVICE_INFORMATION = new ServiceInformation(HOST, PORT);

    @Test
    public void should_delegate_post_call_to_rest_template() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        RestClient restClient = new RestClient(SERVICE_INFORMATION, restTemplate);
        Double body = 123d;

        restClient.post("forecast", body, String.class);

        verify(restTemplate).postForObject("http://"+HOST+":"+PORT, body, String.class);
    }
}