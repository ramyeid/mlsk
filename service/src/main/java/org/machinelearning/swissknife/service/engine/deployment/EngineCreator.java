package org.machinelearning.swissknife.service.engine.deployment;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.ServiceInformation;
import org.machinelearning.swissknife.service.engine.EngineImpl;
import org.machinelearning.swissknife.service.engine.exceptions.EngineCreationException;

import java.io.File;
import java.io.IOException;

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
                            .command("python3", "engine.py", "--port", port)
                            .directory(new File("components/engine"))
                            .start();

        return new ServiceInformation("localhost", port, String.valueOf(process.pid()));
    }
}
