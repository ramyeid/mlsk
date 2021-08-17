package org.mlsk.service.impl.engine;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.impl.EngineImpl;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.buildServiceConfiguration;

public class EngineFactoryTest {

  private EngineFactory engineFactory;

  @BeforeEach
  public void setUp() {
    this.engineFactory = new EngineFactory();
  }

  @Test
  public void should_build_engine() throws ParseException {
    buildServiceConfiguration("", "--engine-ports", "port1", "--logs-path", "logsPath", "-engine-path", "enginePath");
    ServiceInformation serviceInformation = new ServiceInformation("host", "port");

    Engine engine = engineFactory.buildEngine(serviceInformation);

    assertNotNull(engine);
    assertInstanceOf(EngineImpl.class, engine);
  }

}