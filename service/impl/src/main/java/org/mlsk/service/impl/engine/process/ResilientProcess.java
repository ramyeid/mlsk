package org.mlsk.service.impl.engine.process;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mlsk.service.impl.engine.exceptions.EngineCreationException;
import org.mlsk.service.model.ServiceInformation;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mlsk.service.impl.ServiceConfiguration.getEnginePath;
import static org.mlsk.service.impl.ServiceConfiguration.getLogsPath;

public class ResilientProcess {

  private static final Logger LOGGER = Logger.getLogger(EngineFactory.class);

  private Process process;
  private final ServiceInformation serviceInformation;
  private final ProcessBuilder processBuilder;

  public ResilientProcess(ServiceInformation serviceInformation, Runnable onProcessKilled) throws IOException, InterruptedException {
    this(serviceInformation, new ProcessBuilder(), onProcessKilled);
  }

  @VisibleForTesting
  public ResilientProcess(ServiceInformation serviceInformation, ProcessBuilder processBuilder, Runnable onProcessKilled) throws IOException, InterruptedException {
    this.serviceInformation = serviceInformation;
    this.processBuilder = processBuilder;
    launchProcess();
    this.process.onExit().thenAcceptAsync(processIgnored -> onProcessKilled.run());
  }

  public void launchProcess() throws IOException, InterruptedException {
    String port = this.serviceInformation.getPort();
    try {
      LOGGER.info(String.format("[Start] Launching engine with port: %s", port));
      process = processBuilder
          .command("python3", "engine.py", "--port", port, "--logs-path", getLogsPath())
          .directory(new File(getEnginePath()))
          .start();

      process.waitFor(3, TimeUnit.SECONDS);

      if (!process.isAlive()) {
        throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
      }

      LOGGER.info(String.format("Engine launched with pid: %s", process.pid()));
    } finally {
      LOGGER.info(String.format("[End] Launching engine with port: %s", port));
    }
  }
}
