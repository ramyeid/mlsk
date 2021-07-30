package org.mlsk.service.impl.inttest;

import org.mlsk.lib.model.ServiceInformation;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MockEngine {

  private RestTemplate restTemplateMock;
  private boolean isWaitUntilEngineCallEnabled;
  private CountDownLatch waitUntilEngineCallLatch;

  MockEngine() {
    this.isWaitUntilEngineCallEnabled = false;
  }

  public void setRestTemplateMock(RestTemplate restTemplateMock) {
    this.restTemplateMock = restTemplateMock;
  }

  void reset() {
    this.isWaitUntilEngineCallEnabled = false;
    this.waitUntilEngineCallLatch = null;
  }

  void setupWaitUntilEngineCall() {
    this.isWaitUntilEngineCallEnabled = true;
    this.waitUntilEngineCallLatch = new CountDownLatch(1);
  }

  void waitUntilEngineCall() throws InterruptedException {
    waitUntilEngineCallLatch.await();
    setupWaitUntilEngineCall();
  }

  @SafeVarargs
  public final <Request, Result> void onRestTemplatePostReturn(Class<Result> type, MockedRequest<Request, Result>... mockedRequests) {
    when(restTemplateMock.postForObject(any(String.class), any(), eq(type))).thenAnswer((Answer<Result>) invocationOnMock -> {
      Optional<MockedRequest<Request, Result>> mockedRequestOptional = retrieveMatchingRequest(invocationOnMock, mockedRequests);

      if (mockedRequestOptional.isPresent()) {
        MockedRequest<Request, Result> mockedRequest = mockedRequestOptional.get();
        releaseWaitUntilEngineCallLatchIfNecessary();
        throwExceptionIfNecessary(mockedRequest);
        hangEngineIfNecessary(mockedRequest);
        return mockedRequest.result;
      }
      return null;
    });
  }

  void verifyEngineCalledOnResource(String resource, InOrder inOrder) {
    inOrder.verify(restTemplateMock).postForObject(eq(resource), any(), any());
  }

  private <Request, Result> Optional<MockedRequest<Request, Result>> retrieveMatchingRequest(InvocationOnMock invocationOnMock, MockedRequest<Request, Result>[] mockedRequests) {
    String actualResource = invocationOnMock.getArgument(0, String.class);
    Request actualRequest = ((HttpEntity<Request>) invocationOnMock.getArgument(1)).getBody();

    Predicate<MockedRequest<Request, Result>> matchesResource = mockedRequest -> mockedRequest.resource.equals(actualResource);
    Predicate<MockedRequest<Request, Result>> matchesBody = mockedRequest -> mockedRequest.request.equals(actualRequest);

    Optional<MockedRequest<Request, Result>> mockedRequestOptional = Arrays
        .stream(mockedRequests)
        .filter(matchesResource.and(matchesBody))
        .findFirst();
    return mockedRequestOptional;
  }

  private void releaseWaitUntilEngineCallLatchIfNecessary() {
    if (isWaitUntilEngineCallEnabled) {
      waitUntilEngineCallLatch.countDown();
    }
  }

  private <Request, Result> void throwExceptionIfNecessary(MockedRequest<Request, Result> mockedRequest) throws Exception {
    if (mockedRequest.exception != null) {
      throw mockedRequest.exception;
    }
  }

  private <Request, Result> void hangEngineIfNecessary(MockedRequest<Request, Result> mockedRequest) throws InterruptedException {
    if (mockedRequest.shouldHang) {
      mockedRequest.countDownLatch.await();
    }
  }

  static class MockedRequest<Request, Result> {

    final String resource;
    final Request request;
    final Result result;
    final Exception exception;
    final boolean shouldHang;
    final CountDownLatch countDownLatch;

    private MockedRequest(String resource, Request request, Result result, Exception exception, boolean shouldHang) {
      this.resource = resource;
      this.request = request;
      this.result = result;
      this.exception = exception;
      this.shouldHang = shouldHang;
      this.countDownLatch = new CountDownLatch(1);
    }

    public static <Request, Result> MockedRequest<Request, Result> buildHangingMockRequest(ServiceInformation serviceInformation, String endPoint, Request request, Result result) {
      String resource = serviceInformation.getUrl() + endPoint;
      return new MockedRequest<>(resource, request, result, null, true);
    }

    public static <Request, Result> MockedRequest<Request, Result> buildMockRequest(ServiceInformation serviceInformation, String endPoint, Request request, Result result) {
      String resource = serviceInformation.getUrl() + endPoint;
      return new MockedRequest<>(resource, request, result, null, false);
    }

    public static <Request, Result> MockedRequest<Request, Result> buildFailingMockRequest(ServiceInformation serviceInformation, String endPoint, Request request, Exception exception) {
      String resource = serviceInformation.getUrl() + endPoint;
      return new MockedRequest<>(resource, request, null, exception, false);

    }
  }
}
