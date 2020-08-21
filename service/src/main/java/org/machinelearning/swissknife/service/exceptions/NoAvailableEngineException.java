package org.machinelearning.swissknife.service.exceptions;

public class NoAvailableEngineException extends RuntimeException {

    public NoAvailableEngineException(String actionName) {
        super("No available engine to run " + actionName + " please try again later");
    }
}
