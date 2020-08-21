package org.machinelearning.swissknife.service;

import org.machinelearning.swissknife.Engine;
import org.machinelearning.swissknife.lib.rest.ServiceInformation;
import org.machinelearning.swissknife.service.engine.deployment.EngineCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.singletonList;

@Configuration
public class SpringConfiguration {

    @Bean
    public Orchestrator buildOrchestrator() {
        EngineCreator engineCreator = new EngineCreator();
        Engine engine = engineCreator.createEngine(new ServiceInformation("localhost", "6767"));
        return new Orchestrator(singletonList(engine));
    }
}
