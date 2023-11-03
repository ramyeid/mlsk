package org.mlsk.service.impl.orchestrator.request;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestIdGenerator {

  private static final Logger LOGGER = LogManager.getLogger(RequestIdGenerator.class);

  private static long nextId = 1L;

  public static synchronized long nextId() {
    LOGGER.info("Creating a new RequestId: {}", nextId);
    return nextId++;
  }

  @VisibleForTesting
  static synchronized void reset(long initialValue) {
    nextId = initialValue;
  }
}
