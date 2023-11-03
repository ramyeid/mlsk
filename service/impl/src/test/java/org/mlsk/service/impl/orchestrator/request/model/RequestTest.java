package org.mlsk.service.impl.orchestrator.request.model;

import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.Endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {

  @Test
  public void should_return_id_as_hash_of_service_information() {
    Endpoint endpoint = new Endpoint("host", 123L);
    Request request = new Request("action", endpoint);

    String actualId = request.getId();

    String expectedId = String.valueOf(endpoint.hashCode());
    assertEquals(expectedId, actualId);
  }
}