package org.machinelearning.swissknife.service.engine.exceptions;

public class EngineCreationException extends RuntimeException {

    public EngineCreationException(String message) {
        super(String.format("Failed to create engine %s", message));
    }

    public EngineCreationException(Exception cause) {
        super("Failed to create engine", cause);
    }
}
