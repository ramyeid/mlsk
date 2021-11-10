package org.mlsk.service.impl.orchestrator.request.model;

import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {

  @Test
  public void should_return_id_as_hash_of_service_information() {
    ServiceInformation serviceInformation = new ServiceInformation("host", 123L);
    Request request = new Request("action", serviceInformation);

    String actualId = request.getId();

    String expectedId = String.valueOf(serviceInformation.hashCode());
    assertEquals(expectedId, actualId);
  }
}