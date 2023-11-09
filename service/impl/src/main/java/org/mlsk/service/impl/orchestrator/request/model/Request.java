package org.mlsk.service.impl.orchestrator.request.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.model.Endpoint;

import java.util.Objects;
import java.util.concurrent.Semaphore;

public class Request {

  private static final Logger LOGGER = LogManager.getLogger(Request.class);

  private final long requestId;
  private final Endpoint endpoint;
  private final Semaphore semaphore;

  public Request(long requestId, Endpoint endpoint) {
    this.requestId = requestId;
    this.endpoint = endpoint;
    this.semaphore = new Semaphore(1, true);
  }

  public long getRequestId() {
    return requestId;
  }

  public Endpoint getEndpoint() {
    return endpoint;
  }

  public void lock() throws InterruptedException {
    semaphore.acquire();
  }

  public void safeLock() {
    try {
      this.lock();
    } catch (InterruptedException e) {
      LOGGER.info("[{}] Lock threw exception: {}", requestId, e.getMessage());
    }
  }

  public void releaseLock() {
    semaphore.release();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request that = (Request) o;
    return requestId == that.requestId && Objects.equals(endpoint, that.endpoint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, endpoint);
  }

  @Override
  public String toString() {
    return "Request{" +
        "requestId=" + requestId +
        ", endpoint=" + endpoint +
        '}';
  }
}
