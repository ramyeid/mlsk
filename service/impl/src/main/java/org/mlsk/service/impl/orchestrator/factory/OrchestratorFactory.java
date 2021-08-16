package org.mlsk.service.impl.orchestrator.factory;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.service.Engine;
import org.mlsk.service.impl.engine.EngineFactory;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mlsk.service.impl.configuration.ServiceConfiguration.getEnginesServiceInformation;

@Service
public class OrchestratorFactory {

  private final EngineFactory engineFactory;

  @Autowired
  public OrchestratorFactory() {
    this(new EngineFactory());
  }

  @VisibleForTesting
  public OrchestratorFactory(EngineFactory engineFactory) {
    this.engineFactory = engineFactory;
  }

  public Orchestrator buildAndLaunchOrchestrator() {
    List<Engine> engines = getEnginesServiceInformation()
        .stream()
        .map(engineFactory::buildEngine)
        .collect(toList());
    Orchestrator orchestrator = new Orchestrator(engines);
    orchestrator.launchEngines();
    return orchestrator;
  }
}
