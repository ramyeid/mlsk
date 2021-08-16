package org.mlsk.service.impl.orchestrator.factory;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.buildServiceConfiguration;
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
    ServiceInformation engineInfo1 = new ServiceInformation("localhost", "port1");
    ServiceInformation engineInfo2 = new ServiceInformation("localhost", "port2");
    String ports = format("%s,%s", engineInfo1.getPort(), engineInfo2.getPort());
    Engine engine1 = mock(Engine.class);
    Engine engine2 = mock(Engine.class);
    onBuildEngineFactoryReturn(engineInfo1, engine1);
    onBuildEngineFactoryReturn(engineInfo2, engine2);
    buildServiceConfigurationWithEnginePorts(ports);

    orchestratorFactory.buildAndLaunchOrchestrator();

    InOrder inOrder = inOrder(engine1, engine2, engineFactory);
    inOrder.verify(engineFactory).buildEngine(engineInfo1);
    inOrder.verify(engineFactory).buildEngine(engineInfo2);
    inOrder.verify(engine1).launchEngine();
    inOrder.verify(engine2).launchEngine();
    inOrder.verifyNoMoreInteractions();
  }

  private void onBuildEngineFactoryReturn(ServiceInformation serviceInformation, Engine engine) {
    when(engineFactory.buildEngine(serviceInformation)).thenReturn(engine);
  }

  private static void buildServiceConfigurationWithEnginePorts(String ports) throws ParseException {
    buildServiceConfiguration("", "--engine-ports", ports, "--logs-path", "ignored", "-engine-path", "ignored");
  }

}