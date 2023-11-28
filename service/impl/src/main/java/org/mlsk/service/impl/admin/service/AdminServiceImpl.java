package org.mlsk.service.impl.admin.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.service.admin.AdminEngine;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.impl.admin.service.exception.AdminServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.impl.timeseries.service.exception.TimeSeriesAnalysisServiceException;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

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
      return orchestrator.priorityRunOnEngine(engineId, AdminEngine::ping);
    } catch (Exception exception) {
      LOGGER.error(format("[Admin] Exception while pinging `%s`: %s", engineId, exception.getMessage()), exception);
      throw new AdminServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End][Admin] Ping request on engine `{}`", engineId);
    }
  }

  @Override
  public List<EngineDetailResponse> pingAll() {
    try {
      LOGGER.info("[Start][Admin] Ping request on all engine");
      return orchestrator.priorityRunOnAllEngines(AdminEngine::ping);
    } catch (Exception exception) {
      LOGGER.error(format("[Admin] Exception while pinging all engines: %s", exception.getMessage()), exception);
      throw new AdminServiceException(exception.getMessage());
    } finally {
      LOGGER.info("[End][Admin] Ping request on all engine");
    }
  }

  private static TimeSeriesAnalysisServiceException logAndBuildException(Exception exception, long requestId, String action) {
    LOGGER.error(format("[%d] Exception while %s: %s", requestId, action, exception.getMessage()), exception);
    return new TimeSeriesAnalysisServiceException(exception.getMessage());
  }
}
