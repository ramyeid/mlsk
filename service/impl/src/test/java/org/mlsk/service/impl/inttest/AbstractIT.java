package org.mlsk.service.impl.inttest;

import org.mlsk.lib.engine.ResilientEngineProcess;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.orchestrator.factory.OrchestratorFactory;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.buildServiceConfiguration;
import static org.mlsk.service.model.EngineState.OFF;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractIT {

  protected static final ServiceInformation SERVICE_INFO1 = new ServiceInformation("localhost", "port1");
  protected static final ServiceInformation SERVICE_INFO2 = new ServiceInformation("localhost", "port2");
  protected static final String LOGS_PATH = "logsPath";
  protected static final String ENGINE_PATH = "enginePath";

  @Mock
  protected RestTemplate restTemplate;
  @Mock
  protected EngineLauncher engineLauncher;
  @Mock
  protected EngineFactory engineFactory;

  protected Orchestrator orchestrator;
  protected ExecutorService executor;
  protected final MockEngine mockEngine;
  private final List<MockProcess> processes;

  AbstractIT() {
    mockEngine = new MockEngine();
    processes = newArrayList();
  }

  protected void setup(List<ServiceInformation> serviceInformationList) throws Exception {
    String ports = serviceInformationList.stream().map(ServiceInformation::getPort).collect(joining(","));
    buildServiceConfiguration("", "--engine-ports", ports, "--logs-path", LOGS_PATH, "-engine-path", ENGINE_PATH);

    onRestTemplatePostForObjectCallMockEngine();
    setUpEngineFactory();
    setUpEngineLauncher(serviceInformationList);

    executor = Executors.newFixedThreadPool(serviceInformationList.size());
    mockEngine.reset();
    orchestrator = new OrchestratorFactory(engineFactory).buildAndLaunchOrchestrator();
  }

  protected Engine getEngine(int index) {
    return orchestrator.getEngines().get(index);
  }

  protected InOrder buildInOrder() {
    return inOrder(restTemplate, engineLauncher, engineFactory);
  }

  protected void verifyServiceSetup(List<ServiceInformation> serviceInformationList, InOrder inOrder) throws IOException {
    for (ServiceInformation serviceInformation : serviceInformationList) {
      inOrder.verify(engineLauncher).launchEngine(serviceInformation, LOGS_PATH, ENGINE_PATH);
    }
  }

  void verifyRestTemplateCalledOn(String resource, InOrder inOrder) {
    inOrder.verify(restTemplate).postForObject(eq(resource), any(), any());
  }

  private void setUpEngineFactory() {
    when(engineFactory.buildEngine(any())).thenAnswer(invocationOnMock -> {
      ServiceInformation serviceInformation = invocationOnMock.getArgument(0, ServiceInformation.class);
      return buildEngine(serviceInformation);
    });
  }

  private void setUpEngineLauncher(List<ServiceInformation> serviceInformationList) throws IOException {
    for (ServiceInformation serviceInformation : serviceInformationList) {
      MockProcess mockProcess = new MockProcess();
      when(engineLauncher.launchEngine(serviceInformation, LOGS_PATH, ENGINE_PATH)).thenReturn(mockProcess.getProcess());
      processes.add(mockProcess);
    }
  }

  private void onRestTemplatePostForObjectCallMockEngine() {
    when(restTemplate.postForObject(any(String.class), any(), any())).thenAnswer(invocationOnMock -> {
      String actualResource = invocationOnMock.getArgument(0, String.class);
      Object actualRequest = invocationOnMock.getArgument(1, HttpEntity.class).getBody();
      return mockEngine.engineCall(actualResource, actualRequest);
    });
  }

  protected static HttpServerErrorException buildHttpServerErrorException(HttpStatus status, String body) {
    return new HttpServerErrorException(status, "statusText", body.getBytes(), Charset.defaultCharset());
  }

  private Engine buildEngine(ServiceInformation serviceInformation) {
    EngineClientFactory engineClientFactory = buildEngineClientFactory(serviceInformation);
    ResilientEngineProcess resilientEngineProcess = new ResilientEngineProcess(serviceInformation, engineLauncher, LOGS_PATH, ENGINE_PATH);
    return new EngineImpl(engineClientFactory, serviceInformation, resilientEngineProcess, new AtomicReference<>(OFF));
  }

  private EngineClientFactory buildEngineClientFactory(ServiceInformation serviceInformation) {
    RestClient restClient = new RestClient(serviceInformation, restTemplate);

    TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(restClient);

    EngineClientFactory engineClientFactory = spy(new EngineClientFactory());
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation)).thenReturn(timeSeriesAnalysisEngineClient);
    return engineClientFactory;
  }
}
