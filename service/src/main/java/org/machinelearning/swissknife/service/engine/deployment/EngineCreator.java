package org.machinelearning.swissknife.service.engine.deployment;

import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.service.engine.EngineImpl;

public class EngineCreator {

    public Engine createEngine(ServiceInformation serviceInformation) {
        launchEngine();
        return new EngineImpl(serviceInformation);
    }

    private void launchEngine() {
        //for now we suppose that the engine is already launched.
        // for now the EngineCreator only supports creating the engine on localhost
    }


}
