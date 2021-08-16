package org.mlsk.lib.engine;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.ServiceInformation;

import java.util.concurrent.TimeUnit;

public class ResilientEngineProcess {

  private static final Logger LOGGER = LogManager.getLogger(ResilientEngineProcess.class);

  private Process process;
  private final ServiceInformation serviceInformation;
  private final EngineLauncher engineLauncher;
  private final String enginePath;
  private final String logsPath;

  public ResilientEngineProcess(ServiceInformation serviceInformation, String logsPath, String enginePath) {
    this(serviceInformation, new EngineLauncher(), logsPath, enginePath);
  }

  @VisibleForTesting
  public ResilientEngineProcess(ServiceInformation serviceInformation, EngineLauncher engineLauncher, String logsPath, String enginePath) {
    this.serviceInformation = serviceInformation;
    this.engineLauncher = engineLauncher;
    this.enginePath = enginePath;
    this.logsPath = logsPath;
  }

  public void launchEngine(Runnable onProcessKilled) throws Exception {
    try {
      LOGGER.info("[Start] Launching engine with information: {}", serviceInformation);

      process = engineLauncher.launchEngine(serviceInformation, logsPath, enginePath);

      process.waitFor(3, TimeUnit.SECONDS);

      if (!process.isAlive()) {
        throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
      }

      this.process.onExit().thenAcceptAsync(processIgnored -> onProcessKilled.run());

      LOGGER.info("Engine launched with pid: {}", process.pid());
    } finally {
      LOGGER.info("[End] Launching engine with information: {}", serviceInformation);
    }
  }

  public boolean isEngineUp() {
    return process != null && process.isAlive();
  }
}
