package org.mlsk.service.impl.orchestrator.request.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.orchestrator.request.model.Request;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestRegistryTest {

  private RequestRegistry requestRegistry;

  @BeforeEach
  void setUp() {
    requestRegistry = new RequestRegistry();
  }

  @Test
  public void should_return_empty_optional_if_no_request_booked() {

    Optional<Request> request = requestRegistry.getRequest("requestId");

    assertTrue(request.isEmpty());
  }

  @Test
  public void should_return_request_if_request_booked() {
    String requestId = "requestId";
    Request request = new Request("action", new ServiceInformation("host", 123L));
    requestRegistry.addRequest(requestId, request);

    Optional<Request> actualRequest = requestRegistry.getRequest(requestId);

    assertTrue(actualRequest.isPresent());
    assertEquals(request, actualRequest.get());
  }

  @Test
  public void should_return_empty_optional_if_request_booked_has_been_removed() {
    String requestId = "requestId";
    Request request = new Request("action", new ServiceInformation("host", 123L));
    requestRegistry.addRequest(requestId, request);
    requestRegistry.removeRequest(requestId);

    Optional<Request> actualRequest = requestRegistry.getRequest(requestId);

    assertTrue(actualRequest.isEmpty());
  }
}