package org.mlsk.service.impl.orchestrator.factory;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.mlsk.service.impl.setup.ServiceConfiguration.buildServiceConfiguration;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorFactoryTest {

  @Mock
  private EngineFactory engineFactory;

  private OrchestratorFactory orchestratorFactory;

  @BeforeEach
  public void setUp() {
    this.orchestratorFactory = new OrchestratorFactory(engineFactory);
  }

  @Test
  public void should_build_and_launch_engines() throws ParseException {
    Endpoint engineEndpoint1 = new Endpoint("localhost", 6768L);
    Endpoint engineEndpoint2 = new Endpoint("localhost", 6769L);
    String ports = format("%s,%s", engineEndpoint1.getPort(), engineEndpoint2.getPort());
    Engine engine1 = mock(Engine.class);
    Engine engine2 = mock(Engine.class);
    onBuildEngineFactoryReturn(engineEndpoint1, engine1);
    onBuildEngineFactoryReturn(engineEndpoint2, engine2);
    buildServiceConfigurationWithEnginePorts(ports);

    orchestratorFactory.buildAndLaunchOrchestrator();

    InOrder inOrder = inOrder(engine1, engine2, engineFactory);
    inOrder.verify(engineFactory).buildEngine(engineEndpoint1);
    inOrder.verify(engineFactory).buildEngine(engineEndpoint2);
    inOrder.verify(engine1).launchEngine(any());
    inOrder.verify(engine1).markAsReadyForNewRequest();
    inOrder.verify(engine2).launchEngine(any());
    inOrder.verify(engine2).markAsReadyForNewRequest();
    inOrder.verifyNoMoreInteractions();
  }

  private void onBuildEngineFactoryReturn(Endpoint endpoint, Engine engine) {
    when(engineFactory.buildEngine(endpoint)).thenReturn(engine);
  }

  private static void buildServiceConfigurationWithEnginePorts(String ports) throws ParseException {
    buildServiceConfiguration("", "--engine-ports", ports, "--logs-path", "ignored", "-engine-path", "ignored");
  }

}