package org.mlsk.service.impl.orchestrator.request.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestIdGeneratorTest {

  @Test
  public void should_return_next_id() {
    RequestIdGenerator.reset(1L);

    long firstId = RequestIdGenerator.nextId();
    long secondId = RequestIdGenerator.nextId();

    assertEquals(1L, firstId);
    assertEquals(2L, secondId);
  }
}