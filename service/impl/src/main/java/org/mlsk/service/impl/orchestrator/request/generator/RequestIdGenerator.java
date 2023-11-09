package org.mlsk.service.impl.orchestrator.request.generator;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

public class RequestIdGenerator {

  private static final Logger LOGGER = LogManager.getLogger(RequestIdGenerator.class);

  private static AtomicLong NEXT_ID = new AtomicLong(1L);

  public static long nextId() {
    LOGGER.info("Creating a new RequestId: {}", NEXT_ID);
    return NEXT_ID.getAndIncrement();
  }

  @VisibleForTesting
  public static void reset(long initialValue) {
    NEXT_ID.set(initialValue);
  }
}
