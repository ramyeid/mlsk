package org.mlsk.service.impl.orchestrator.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RequestIdRegistryTest {

  private RequestIdRegistry requestIdRegistry;

  @BeforeEach
  void setUp() {
    requestIdRegistry = new RequestIdRegistry();
  }

  @Test
  public void should_return_empty_optional_if_no_engine_information_booked_for_request() {

    Optional<ServiceInformation> engineInformation = requestIdRegistry.getEngineInformation("requestId");

    assertTrue(engineInformation.isEmpty());
  }

  @Test
  public void should_return_service_information_if_engine_information_booked_for_request() {
    ServiceInformation serviceInformation = new ServiceInformation("host", 123L);
    String requestId = "requestId";
    requestIdRegistry.addRequestIdAndEngineInformation(requestId, serviceInformation);

    Optional<ServiceInformation> engineInformation = requestIdRegistry.getEngineInformation(requestId);

    assertFalse(engineInformation.isEmpty());
    assertEquals(serviceInformation, engineInformation.get());
  }

  @Test
  public void should_return_empty_optional_if_engine_information_booked_for_request_has_been_removed_() {
    ServiceInformation serviceInformation = new ServiceInformation("host", 123L);
    String requestId = "requestId";
    requestIdRegistry.addRequestIdAndEngineInformation(requestId, serviceInformation);
    requestIdRegistry.removeRequestId(requestId);

    Optional<ServiceInformation> engineInformation = requestIdRegistry.getEngineInformation("requestId");

    assertTrue(engineInformation.isEmpty());
  }
}