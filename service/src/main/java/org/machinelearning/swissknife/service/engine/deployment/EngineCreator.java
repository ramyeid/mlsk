package org.machinelearning.swissknife.service.engine.deployment;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.service.engine.EngineImpl;
import org.machinelearning.swissknife.service.engine.exceptions.EngineCreationException;

import java.io.File;
import java.io.IOException;

import static org.machinelearning.swissknife.service.Application.ENGINE_PATH;
import static org.machinelearning.swissknife.service.Application.LOGS_PATH;

public class EngineCreator {

    private final ProcessBuilder processBuilder;

    public EngineCreator() {
        this.processBuilder = new ProcessBuilder();
    }

    @VisibleForTesting
    public EngineCreator(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    public Engine createEngine(String port) throws EngineCreationException {
        try {
            ServiceInformation serviceInformation = launchEngine(port);
            return new EngineImpl(serviceInformation);
        } catch (IOException exception) {
            throw new EngineCreationException(exception);
        }
    }

    private ServiceInformation launchEngine(String port) throws IOException {
        Process process = processBuilder
                .command("python3", "engine.py", "--port", port, "--logsPath", LOGS_PATH)
                .directory(new File(ENGINE_PATH))
                .start();

        if (!process.isAlive()) {
            throw new EngineCreationException(new String(process.getErrorStream().readAllBytes()));
        }

        return new ServiceInformation("localhost", port, String.valueOf(process.pid()));
    }
}
