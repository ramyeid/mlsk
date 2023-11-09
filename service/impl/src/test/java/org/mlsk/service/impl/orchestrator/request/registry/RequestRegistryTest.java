package org.mlsk.service.impl.orchestrator.request.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.Endpoint;
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

    Optional<Request> request = requestRegistry.get(1L);

    assertTrue(request.isEmpty());
  }

  @Test
  public void should_return_request_if_request_booked() {
    long requestId1 = 1L;
    long requestId2 = 2L;
    requestRegistry.register(requestId1, new Endpoint("host", 123L));
    requestRegistry.register(requestId2, new Endpoint("host", 123L));

    Optional<Request> actualRequest = requestRegistry.get(requestId1);

    Request expectedRequest = new Request(requestId1, new Endpoint("host", 123L));
    assertTrue(actualRequest.isPresent());
    assertEquals(expectedRequest, actualRequest.get());
  }

  @Test
  public void should_return_empty_optional_if_request_booked_has_been_removed() {
    long requestId = 3L;
    requestRegistry.register(requestId, new Endpoint("host", 123L));
    requestRegistry.release(requestId);

    Optional<Request> actualRequest = requestRegistry.get(requestId);

    assertTrue(actualRequest.isEmpty());
  }

  @Test
  public void should_release_all_requests_of_endpoint() {
    Endpoint endpoint1 = new Endpoint("host", 123L);
    Endpoint endpoint2 = new Endpoint("host1", 123L);
    Endpoint endpoint3 = new Endpoint("host2", 123L);
    requestRegistry.register(1L, endpoint1);
    requestRegistry.register(2L, endpoint2);
    requestRegistry.register(3L, endpoint1);
    requestRegistry.register(4L, endpoint3);
    requestRegistry.register(5L, endpoint1);

    requestRegistry.releaseAll(endpoint1);

    assertTrue(requestRegistry.get(1L).isEmpty());
    assertEquals(new Request(2L, endpoint2), requestRegistry.get(2L).get());
    assertTrue(requestRegistry.get(3L).isEmpty());
    assertEquals(new Request(4L, endpoint3), requestRegistry.get(4L).get());
    assertTrue(requestRegistry.get(5L).isEmpty());
  }
}