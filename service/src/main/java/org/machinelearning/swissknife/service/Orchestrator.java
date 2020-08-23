package org.machinelearning.swissknife.service;

import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.service.exceptions.NoAvailableEngineException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Orchestrator {

    private final List<Engine> engines;

    public Orchestrator(List<Engine> engines) {
        this.engines = engines;
    }

    public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
        Optional<Engine> availableEngine = this.engines.stream().filter(engine -> engine.getState().equals(EngineState.WAITING)).findFirst();

        if(availableEngine.isEmpty()) {
            throw new NoAvailableEngineException(actionName);
        }
        return action.apply(availableEngine.get());
    }
}
