package org.mlsk.service.impl.inttest;

import org.mlsk.api.engine.classifier.client.ClassifierEngineApi;
import org.mlsk.api.engine.timeseries.client.TimeSeriesAnalysisEngineApi;
import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.factory.OrchestratorFactory;
import org.mlsk.service.impl.orchestrator.request.generator.RequestIdGenerator;
import org.mlsk.service.model.engine.EngineState;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mlsk.lib.rest.RestTemplateFactory.buildRestTemplate;
import static org.mlsk.service.impl.inttest.AbstractIT.RequestEntityMatcher.isSameResource;
import static org.mlsk.service.impl.setup.ServiceConfiguration.buildServiceConfiguration;
import static org.mlsk.service.model.engine.EngineState.OFF;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractIT {

  protected static final Endpoint ENDPOINT1 = new Endpoint("localhost", 6768L);
  protected static final Endpoint ENDPOINT2 = new Endpoint("localhost", 6767L);
  protected static final String LOGS_PATH = "logsPath";
  protected static final String ENGINE_PATH = "enginePath";
  protected static final String LOG_LEVEL = "logLevel";
  protected static final String ENGINE_LOG_LEVEL = "engineLogLevel";

  private RestTemplate restTemplateSpy;
  @Mock
  protected EngineLauncher engineLauncher;
  @Mock
  protected EngineFactory engineFactory;

  protected Orchestrator orchestrator;
  protected ExecutorService executor;
  protected final MockEngine mockEngine;
  private final List<MockProcess> processes;

  protected AbstractIT() {
    mockEngine = new MockEngine();
    processes = newArrayList();
  }

  protected void setup(List<Endpoint> endpoints) throws Exception {
    String ports = endpoints.stream().map(Endpoint::getPort).map(Object::toString).collect(joining(","));
    buildServiceConfiguration("", "--engine-ports", ports, "--logs-path", LOGS_PATH, "-engine-path", ENGINE_PATH, "--log-level", LOG_LEVEL, "--engine-log-level", ENGINE_LOG_LEVEL);

    restTemplateSpy = spy(buildRestTemplate());
    onRestTemplatePostForObjectCallMockEngine();
    setUpEngineFactory();
    setUpEngineLauncher(endpoints);
    RequestIdGenerator.reset(1L);

    executor = Executors.newFixedThreadPool(endpoints.size());
    mockEngine.reset();
    orchestrator = new OrchestratorFactory(engineFactory).buildAndLaunchOrchestrator();
  }

  protected <T> CompletableFuture<T> async(Supplier<T> supplier) {
    return supplyAsync(supplier, executor);
  }

  protected <T> void ignoreException(Supplier<T> supplier) {
    try {
      supplier.get();
      fail("this method was expected to fail");
    } catch (Exception ignored) {
    }
  }

  protected InOrder buildInOrder() {
    return inOrder(restTemplateSpy, engineLauncher, engineFactory);
  }

  protected void verifyServiceSetup(List<Endpoint> endpoints, InOrder inOrder) throws IOException {
    for (Endpoint endpoint : endpoints) {
      inOrder.verify(engineLauncher).launchEngine(endpoint, LOGS_PATH, ENGINE_PATH, ENGINE_LOG_LEVEL);
    }
  }

  protected void verifyRestTemplateCalledOn(String resource, InOrder inOrder) {
    inOrder.verify(restTemplateSpy).exchange(isSameResource(resource), any(ParameterizedTypeReference.class));
  }

  protected void assertOnEngineState(EngineState... states) {
    for (int i = 0; i < states.length; ++i) {
      assertEquals(states[i], orchestrator.getEngines().get(i).getState());
    }
  }

  private void setUpEngineFactory() {
    when(engineFactory.buildEngine(any())).thenAnswer(invocationOnMock -> {
      Endpoint endpoint = invocationOnMock.getArgument(0, Endpoint.class);
      return buildEngine(endpoint);
    });
  }

  private void setUpEngineLauncher(List<Endpoint> endpoints) throws IOException {
    for (Endpoint endpoint : endpoints) {
      MockProcess mockProcess = new MockProcess();
      when(engineLauncher.launchEngine(endpoint, LOGS_PATH, ENGINE_PATH, ENGINE_LOG_LEVEL)).thenReturn(mockProcess.getProcess());
      processes.add(mockProcess);
    }
  }

  private void onRestTemplatePostForObjectCallMockEngine() {
    doAnswer(invocationOnMock -> {
      RequestEntity.UriTemplateRequestEntity<?> requestEntity = invocationOnMock.getArgument(0, RequestEntity.UriTemplateRequestEntity.class);
      Object actualRequest = requestEntity.getBody();
      String actualResource = requestEntity.getUriTemplate();
      return mockEngine.engineCall(actualResource, actualRequest);
    }).when(restTemplateSpy).exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  protected static HttpServerErrorException buildHttpServerErrorException(HttpStatus status, String body) {
    return new HttpServerErrorException(status, "statusText", body.getBytes(), Charset.defaultCharset());
  }

  private Engine buildEngine(Endpoint endpoint) {
    EngineClientFactory engineClientFactory = buildEngineClientFactory(endpoint);
    ResilientEngineProcess resilientEngineProcess = new ResilientEngineProcess(endpoint, engineLauncher, LOGS_PATH, ENGINE_PATH, ENGINE_LOG_LEVEL);
    return new EngineImpl(engineClientFactory, endpoint, resilientEngineProcess, new AtomicReference<>(OFF));
  }

  private EngineClientFactory buildEngineClientFactory(Endpoint endpoint) {
    ClassifierEngineApi classifierEngineApi = new ClassifierEngineApi(new org.mlsk.api.engine.classifier.client.ApiClient(restTemplateSpy).setBasePath(endpoint.getUrl()));
    TimeSeriesAnalysisEngineApi timeSeriesAnalysisEngineApi = new TimeSeriesAnalysisEngineApi(new org.mlsk.api.engine.timeseries.client.ApiClient(restTemplateSpy).setBasePath(endpoint.getUrl()));

    EngineClientFactory engineClientFactory = mock(EngineClientFactory.class);
    when(engineClientFactory.buildClassifierClient(endpoint)).thenReturn(classifierEngineApi);
    when(engineClientFactory.buildTimeSeriesAnalysisClient(endpoint)).thenReturn(timeSeriesAnalysisEngineApi);
    return engineClientFactory;
  }

  public static class RequestEntityMatcher implements ArgumentMatcher<RequestEntity.UriTemplateRequestEntity<?>> {

    private final String resource;

    private RequestEntityMatcher(String resource) {
      this.resource = resource;
    }

    static RequestEntity.UriTemplateRequestEntity<?> isSameResource(String resource) {
      return argThat(new RequestEntityMatcher(resource));
    }

    @Override
    public boolean matches(RequestEntity.UriTemplateRequestEntity<?> right) {
      return resource.equals(right.getUriTemplate());
    }
  }
}
