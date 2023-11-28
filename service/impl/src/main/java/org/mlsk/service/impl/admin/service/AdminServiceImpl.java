package org.mlsk.service.impl.admin.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.admin.AdminEngine;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.impl.admin.service.exception.AdminServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.mlsk.service.model.admin.utils.AdminConstants.PING;

@Service
public class AdminServiceImpl implements AdminService {

  private static final Logger LOGGER = LogManager.getLogger(AdminServiceImpl.class);

  private final Orchestrator orchestrator;

  @Autowired
  public AdminServiceImpl(Orchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @Override
  public EngineDetailResponse ping(int engineId) {
    try {
      LOGGER.info("[Start][Admin] Ping request on engine `{}`", engineId);
      return orchestrator.priorityRunOnEngine(engineId, PING, AdminEngine::ping);
    } catch (Exception exception) {
      throw logAndBuildException(exception, format("pinging: %s", engineId));
    } finally {
      LOGGER.info("[End][Admin] Ping request on engine `{}`", engineId);
    }
  }

  @Override
  public List<EngineDetailResponse> pingAll() {
    try {
      LOGGER.info("[Start][Admin] Ping request on all engine");
      return orchestrator.priorityRunOnAllEngines(PING, AdminEngine::ping);
    } catch (Exception exception) {
      throw logAndBuildException(exception, "pinging all engines");
    } finally {
      LOGGER.info("[End][Admin] Ping request on all engine");
    }
  }

  private static AdminServiceException logAndBuildException(Exception exception, String action) {
    LOGGER.error(format("[Admin] Exception while %s: %s", action, exception.getMessage()), exception);
    return new AdminServiceException(exception.getMessage());
  }
}
