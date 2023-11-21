package org.mlsk.lib.engine.launcher;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.model.Endpoint;

import java.io.File;
import java.io.IOException;

import static java.lang.String.valueOf;

public class EngineLauncher {

  private final ProcessBuilder processBuilder;

  public EngineLauncher() {
    this(new ProcessBuilder());
  }

  @VisibleForTesting
  public EngineLauncher(ProcessBuilder processBuilder) {
    this.processBuilder = processBuilder;
  }

    public Process launchEngine(Endpoint endpoint, String logsPath, String enginePath) throws IOException {
    return processBuilder
        .command("python3", "engine_server.py", "--port", valueOf(endpoint.getPort()), "--logs-path", logsPath, "--log-level", "INFO")
        .directory(new File(enginePath))
        .start();
  }
}
