package org.machinelearning.swissknife.service;

import org.apache.log4j.Logger;
import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.model.EngineState;
import org.machinelearning.swissknife.service.controllers.TimeSeriesAnalysisController;
import org.machinelearning.swissknife.service.exceptions.NoAvailableEngineException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Orchestrator {

    private static final Logger LOGGER = Logger.getLogger(TimeSeriesAnalysisController.class);

    private final List<Engine> engines;

    public Orchestrator(List<Engine> engines) {
        this.engines = engines;
    }

    public <Result> Result runOnEngine(Function<Engine, Result> action, String actionName) {
        Optional<Engine> availableEngineOptional = this.engines.stream().filter(engine -> engine.getState().equals(EngineState.WAITING)).findFirst();

        if(availableEngineOptional.isEmpty()) {
            LOGGER.error(String.format("No Engine available to treat %s", actionName));
            throw new NoAvailableEngineException(actionName);
        }

        Engine availableEngine = availableEngineOptional.get();
        LOGGER.info(String.format("Request %s will be treated on engine: %s", actionName, availableEngine.getServiceInformation()));
        return action.apply(availableEngine);
    }
}
