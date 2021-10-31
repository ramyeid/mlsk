package org.mlsk.service.impl.orchestrator.request;

import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Objects;

import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestIdGeneratorTest {

  @Test
  public void should_generate_unique_id_with_date_and_service_information() {
    RequestIdGenerator requestIdGenerator = new RequestIdGenerator();
    ServiceInformation serviceInformation = new ServiceInformation("host", 1234L);
    Instant currentInstant = Instant.now();

    try (MockedStatic<Instant> instantMockedStatic = Mockito.mockStatic(Instant.class)) {
      instantMockedStatic.when(Instant::now).thenReturn(currentInstant);

      String actualRequestId = requestIdGenerator.generateRequestId(serviceInformation);

      assertEquals(buildExpectedRequestId(serviceInformation, currentInstant), actualRequestId);
    }
  }

  private static String buildExpectedRequestId(ServiceInformation serviceInformation, Instant currentInstant) {
    return valueOf(Objects.hash(currentInstant.toEpochMilli(), serviceInformation.getHost(), serviceInformation.getPort()));
  }
}