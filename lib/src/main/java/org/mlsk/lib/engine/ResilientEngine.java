package org.mlsk.lib.engine;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.ServiceInformation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ResilientEngine {

  private static final Logger LOGGER = Logger.getLogger(ResilientEngine.class);

  private Process process;
  private final ServiceInformation serviceInformation;
  private final EngineLauncher engineLauncher;
  private final String enginePath;
  private final String logsPath;

  public ResilientEngine(ServiceInformation serviceInformation, String logsPath, String enginePath, Runnable onProcessKilled) throws IOException, InterruptedException {
    this(serviceInformation, new EngineLauncher(), logsPath, enginePath, onProcessKilled);
  }

  @VisibleForTesting
  public ResilientEngine(ServiceInformation serviceInformation, EngineLauncher engineLauncher, String logsPath, String enginePath, Runnable onProcessKilled) throws IOException, InterruptedException {
    this.serviceInformation = serviceInformation;
    this.engineLauncher = engineLauncher;
    this.enginePath = enginePath;
    this.logsPath = logsPath;
    launchEngine();
    this.process.onExit().thenAcceptAsync(processIgnored -> onProcessKilled.run());
  }

  public void launchEngine() throws IOException, InterruptedException {
    try {
      LOGGER.info(String.format("[Start] Launching engine with information: %s", serviceInformation));

      process = engineLauncher.launchEngine(serviceInformation, logsPath, enginePath);

      process.waitFor(3, TimeUnit.SECONDS);

      if (!process.isAlive()) {
        throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
      }

      LOGGER.info(String.format("Engine launched with pid: %s", process.pid()));
    } finally {
      LOGGER.info(String.format("[End] Launching engine with information: %s", serviceInformation));
    }
  }
}
