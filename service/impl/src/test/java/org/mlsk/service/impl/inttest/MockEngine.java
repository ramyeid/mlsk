package org.mlsk.service.impl.inttest;

import org.mlsk.lib.model.Endpoint;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

public class MockEngine {

  private final List<MockedRequest> mockedRequests;
  private boolean isWaitUntilEngineCallEnabled;
  private CountDownLatch waitUntilEngineCallLatch;

  MockEngine() {
    this.isWaitUntilEngineCallEnabled = false;
    this.mockedRequests = newArrayList();
  }

  public void registerRequests(MockedRequest... mockedRequestsIn) {
    mockedRequests.addAll(List.of(mockedRequestsIn));
  }

  public void overrideRequests(MockedRequest... mockedRequestsIn) {
    reset();
    registerRequests(mockedRequestsIn);
  }

  void reset() {
    this.isWaitUntilEngineCallEnabled = false;
    this.waitUntilEngineCallLatch = null;
    this.mockedRequests.clear();
  }

  public void setupWaitUntilEngineCall() {
    this.isWaitUntilEngineCallEnabled = true;
    this.waitUntilEngineCallLatch = new CountDownLatch(1);
  }

  public void waitUntilEngineCall() throws InterruptedException {
    waitUntilEngineCallLatch.await();
    setupWaitUntilEngineCall();
  }

  public Object engineCall(String actualResource, Object actualRequest) throws Exception {
    Optional<MockedRequest> mockedRequestOptional = retrieveMatchingRequest(actualResource, actualRequest);

    if (mockedRequestOptional.isPresent()) {
      MockedRequest mockedRequest = mockedRequestOptional.get();
      releaseWaitUntilEngineCallLatchIfNecessary();
      throwExceptionIfNecessary(mockedRequest);
      hangEngineIfNecessary(mockedRequest);
      return mockedRequest.result;
    }
    return null;
  }

  private Optional<MockedRequest> retrieveMatchingRequest(String actualResource, Object actualRequest) {
    Predicate<MockedRequest> matchesResource = mockedRequest -> mockedRequest.resource.equals(actualResource);
    Predicate<MockedRequest> matchesBody = mockedRequest -> (mockedRequest.request == null && actualRequest == null) || (mockedRequest.request != null && mockedRequest.request.equals(actualRequest));

    return mockedRequests
        .stream()
        .filter(matchesResource.and(matchesBody))
        .findFirst();
  }

  private void releaseWaitUntilEngineCallLatchIfNecessary() {
    if (isWaitUntilEngineCallEnabled) {
      waitUntilEngineCallLatch.countDown();
    }
  }

  private void throwExceptionIfNecessary(MockedRequest mockedRequest) throws Exception {
    if (mockedRequest.exception != null) {
      throw mockedRequest.exception;
    }
  }

  private void hangEngineIfNecessary(MockedRequest mockedRequest) throws InterruptedException {
    if (mockedRequest.shouldHang) {
      mockedRequest.countDownLatch.await();
    }
  }

  public static class MockedRequest {

    final String resource;
    final Object request;
    final Object result;
    final Exception exception;
    final boolean shouldHang;
    final CountDownLatch countDownLatch;

    private MockedRequest(String resource, Object request, Object result, Exception exception, boolean shouldHang) {
      this.resource = resource;
      this.request = request;
      this.result = result;
      this.exception = exception;
      this.shouldHang = shouldHang;
      this.countDownLatch = new CountDownLatch(1);
    }

    public void releaseLatch() {
      countDownLatch.countDown();
    }

    public static MockedRequest buildHangingMockRequest(Endpoint endpoint, String endPoint, Object request, Object result) {
      String resource = endpoint.getUrl() + endPoint;
      return new MockedRequest(resource, request, result, null, true);
    }

    public static MockedRequest buildMockRequest(Endpoint endpoint, String endPoint, Object request, Object result) {
      String resource = endpoint.getUrl() + endPoint;
      return new MockedRequest(resource, request, result, null, false);
    }

    public static MockedRequest buildFailingMockRequest(Endpoint endpoint, String endPoint, Object request, Exception exception) {
      String resource = endpoint.getUrl() + endPoint;
      return new MockedRequest(resource, request, null, exception, false);

    }
  }
}
