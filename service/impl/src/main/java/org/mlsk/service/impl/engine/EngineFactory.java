package org.mlsk.service.impl.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.engine.impl.EngineImpl;


public class EngineFactory {

  private static final Logger LOGGER = LogManager.getLogger(EngineFactory.class);

  public Engine buildEngine(Endpoint endpoint) {
    LOGGER.info("Creating engine: {}", endpoint);
    return new EngineImpl(endpoint);
  }
}
