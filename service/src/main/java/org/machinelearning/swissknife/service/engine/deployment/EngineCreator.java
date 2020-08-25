package org.machinelearning.swissknife.service.engine.deployment;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.service.ServiceConfiguration;
import org.machinelearning.swissknife.service.engine.EngineImpl;
import org.machinelearning.swissknife.service.engine.exceptions.EngineCreationException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class EngineCreator {

    private static final Logger LOGGER = Logger.getLogger(EngineCreator.class);

    private final ProcessBuilder processBuilder;
    private final String logsPath;
    private final File enginePath;

    public EngineCreator() {
        this.processBuilder = new ProcessBuilder();
        this.logsPath = ServiceConfiguration.getLogsPath();
        this.enginePath = new File(ServiceConfiguration.getEnginePath());
    }

    @VisibleForTesting
    public EngineCreator(ProcessBuilder processBuilder, String logsPath, File enginePath) {
        this.processBuilder = processBuilder;
        this.logsPath = logsPath;
        this.enginePath = enginePath;
    }

    public Engine createEngine(String port) throws EngineCreationException {
        try {
            LOGGER.info(String.format("[Start] Creating engine with port: %s", port));
            ServiceInformation serviceInformation = launchEngine(port);
            return new EngineImpl(serviceInformation);
        } catch (Exception exception) {
            LOGGER.error(String.format("Error while Launching engine with port: %s", port), exception);
            throw new EngineCreationException(exception);
        } finally {
            LOGGER.info(String.format("[End] Creating engine with port: %s", port));
        }
    }

    private ServiceInformation launchEngine(String port) throws IOException, InterruptedException {
        try {
            LOGGER.info(String.format("[Start] Launching engine with port: %s", port));
            Process process = processBuilder
                    .command("python3", "engine.py", "--port", port, "--logs-path", logsPath)
                    .directory(enginePath)
                    .start();

            process.waitFor(3, TimeUnit.SECONDS);

            if (!process.isAlive()) {
                throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
            }

            ServiceInformation engineInformation = new ServiceInformation("localhost", port, String.valueOf(process.pid()));
            LOGGER.info(String.format("Engine launched with information: %s", engineInformation));
            return engineInformation;
        } finally {
            LOGGER.info(String.format("[End] Launching engine with port: %s", port));
        }
    }
}
