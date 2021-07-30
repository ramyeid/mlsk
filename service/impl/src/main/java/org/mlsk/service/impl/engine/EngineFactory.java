package org.mlsk.service.impl.engine;

import org.apache.log4j.Logger;
import org.mlsk.service.Engine;
import org.mlsk.lib.engine.EngineCreationException;
import org.mlsk.service.impl.engine.impl.EngineImpl;
import org.mlsk.lib.model.ServiceInformation;


public class EngineFactory {

  private static final Logger LOGGER = Logger.getLogger(EngineFactory.class);

  public static Engine createEngine(String port) throws EngineCreationException {
    try {
      LOGGER.info(String.format("[Start] Creating engine with port: %s", port));
      return new EngineImpl(new ServiceInformation("localhost", port));
    } catch (Exception exception) {
      LOGGER.error(String.format("Error while creating engine with port: %s", port), exception);
      throw new EngineCreationException(exception);
    } finally {
      LOGGER.info(String.format("[End] Creating engine with port: %s", port));
    }
  }
}
