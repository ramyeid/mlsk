package org.mlsk.lib.engine;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.lib.engine.launcher.EngineLauncher;
import org.mlsk.lib.model.Endpoint;

import java.util.concurrent.TimeUnit;

public class ResilientEngineProcess {

  private static final Logger LOGGER = LogManager.getLogger(ResilientEngineProcess.class);

  private Process process;
  private final Endpoint endpoint;
  private final EngineLauncher engineLauncher;
  private final String enginePath;
  private final String logsPath;
  private final String engineLogLevel;

  public ResilientEngineProcess(Endpoint endpoint, String logsPath, String enginePath, String engineLogLevel) {
    this(endpoint, new EngineLauncher(), logsPath, enginePath, engineLogLevel);
  }

  @VisibleForTesting
  public ResilientEngineProcess(Endpoint endpoint, EngineLauncher engineLauncher, String logsPath, String enginePath, String engineLogLevel) {
    this.endpoint = endpoint;
    this.engineLauncher = engineLauncher;
    this.enginePath = enginePath;
    this.logsPath = logsPath;
    this.engineLogLevel = engineLogLevel;
  }

  public void launchEngine(Runnable onProcessKilled) throws Exception {
    try {
      LOGGER.info("[Start] Launching engine with endpoint: {}", endpoint);

      process = engineLauncher.launchEngine(endpoint, logsPath, enginePath, engineLogLevel);

      process.waitFor(3, TimeUnit.SECONDS);

      if (!process.isAlive()) {
        throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
      }

      this.process.onExit().thenAcceptAsync(processIgnored -> onProcessKilled.run());

      LOGGER.info("Engine launched with pid: {}", process.pid());
    } finally {
      LOGGER.info("[End] Launching engine with endpoint: {}", endpoint);
    }
  }

  public boolean isEngineUp() {
    return process != null && process.isAlive();
  }
}
