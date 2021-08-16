package org.mlsk.service.impl.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.impl.EngineImpl;


public class EngineFactory {

  private static final Logger LOGGER = LogManager.getLogger(EngineFactory.class);

  public Engine buildEngine(ServiceInformation serviceInformation) {
    LOGGER.info("Creating engine: {}", serviceInformation);
    return new EngineImpl(serviceInformation);
  }
}
