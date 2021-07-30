package org.mlsk.lib.engine.launcher;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.lib.rest.IServiceInformation;

import java.io.File;
import java.io.IOException;

public class EngineLauncher {

  private final ProcessBuilder processBuilder;

  public EngineLauncher() {
    this(new ProcessBuilder());
  }

  @VisibleForTesting
  public EngineLauncher(ProcessBuilder processBuilder) {
    this.processBuilder = processBuilder;
  }

    public Process launchEngine(IServiceInformation serviceInformation, String logsPath, String enginePath) throws IOException {
    return processBuilder
        .command("python3", "engine.py", "--port", serviceInformation.getPort(), "--logs-path", logsPath)
        .directory(new File(enginePath))
        .start();
  }
}
