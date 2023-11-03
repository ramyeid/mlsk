package org.mlsk.service.impl.orchestrator.request;

import static org.junit.jupiter.api.Assertions.*;

public class RequestIdGeneratorTest {

  public void should_generate_and_return_ids() {
    RequestIdGenerator.reset(1L);

    long firstId = RequestIdGenerator.nextId();
    long secondId = RequestIdGenerator.nextId();

    assertEquals(1L, firstId);
    assertEquals(2L, secondId);
  }
}