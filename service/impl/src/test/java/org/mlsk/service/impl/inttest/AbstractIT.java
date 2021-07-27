package org.mlsk.service.impl.inttest;

import org.mlsk.lib.engine.ResilientEngine;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.lib.rest.RestClient;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.Orchestrator;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.impl.engine.client.timeseries.TimeSeriesAnalysisEngineClient;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.service.impl.engine.impl.timeseries.TimeSeriesAnalysisEngineCaller;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.mlsk.service.model.EngineState.WAITING;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractIT {

  protected static final ServiceInformation SERVICE_INFO1 = new ServiceInformation("host1", "port1");
  protected static final ServiceInformation SERVICE_INFO2 = new ServiceInformation("host2", "port2");
  protected static final String LOGS_PATH = "logsPath";
  protected static final String ENGINE_PATH = "enginePath";

  @Mock
  protected RestTemplate restTemplate;
  @Mock
  protected EngineLauncher engineLauncher;
  @Mock
  protected Runnable onProcessKilled;

  protected final MockEngine mockEngine;
  protected final List<Process> mockProcesses;
  protected List<Engine> engines;

  AbstractIT() {
    mockEngine = new MockEngine();
    mockProcesses = newArrayList();
  }

  protected void setup() {
    mockEngine.setRestTemplateMock(restTemplate);
    mockEngine.reset();
  }

  protected Orchestrator buildOrchestrator(List<ServiceInformation> serviceInformationList) throws IOException {
    setUpEngineLauncher(serviceInformationList);
    engines = serviceInformationList.stream().map(this::buildEngine).collect(toList());
    return new Orchestrator(engines);
  }

  protected InOrder buildInOrder() {
    return inOrder(restTemplate, engineLauncher, onProcessKilled);
  }

  protected void verifyServiceSetup(List<ServiceInformation> serviceInformationList, InOrder inOrder) throws IOException {
    for (ServiceInformation serviceInformation : serviceInformationList) {
      inOrder.verify(engineLauncher).launchEngine(serviceInformation, LOGS_PATH, ENGINE_PATH);
    }
  }

  private void setUpEngineLauncher(List<ServiceInformation> serviceInformationList) throws IOException {
    for (ServiceInformation serviceInformation : serviceInformationList) {
      Process process = mock(Process.class);
      when(engineLauncher.launchEngine(serviceInformation, LOGS_PATH, ENGINE_PATH)).thenReturn(process);
      when(process.isAlive()).thenReturn(true);
      when(process.onExit()).thenReturn(CompletableFuture.supplyAsync(() -> null));
    }
  }

  protected static HttpServerErrorException buildHttpServerErrorException(HttpStatus status, String body) {
    return new HttpServerErrorException(status, "statusText", body.getBytes(), Charset.defaultCharset());
  }

  private Engine buildEngine(ServiceInformation serviceInformation) {
    try {
      EngineClientFactory engineClientFactory = buildEngineClientFactory(serviceInformation);

      TimeSeriesAnalysisEngineCaller timeSeriesAnalysisEngineCaller = new TimeSeriesAnalysisEngineCaller(serviceInformation, engineClientFactory);

      ResilientEngine resilientEngine = new ResilientEngine(serviceInformation, engineLauncher, LOGS_PATH, ENGINE_PATH, onProcessKilled);
      return new EngineImpl(resilientEngine, serviceInformation, new AtomicReference<>(WAITING), timeSeriesAnalysisEngineCaller);
    } catch (Exception exception) {
      throw new RuntimeException("Error while building engine for test", exception);
    }
  }

  private EngineClientFactory buildEngineClientFactory(ServiceInformation serviceInformation) {
    RestClient restClient = new RestClient(serviceInformation, restTemplate);

    TimeSeriesAnalysisEngineClient timeSeriesAnalysisEngineClient = new TimeSeriesAnalysisEngineClient(restClient);

    EngineClientFactory engineClientFactory = spy(new EngineClientFactory());
    when(engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation)).thenReturn(timeSeriesAnalysisEngineClient);
    return engineClientFactory;
  }
}
