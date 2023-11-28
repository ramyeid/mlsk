package org.mlsk.service.impl.admin.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mlsk.api.service.admin.api.AdminApi;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.impl.admin.api.mapper.EngineDetailResponseMapper;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@RestController
public class AdminApiImpl implements AdminApi {

  private static final Logger LOGGER = LogManager.getLogger(AdminApiImpl.class);

  private final AdminService service;


  @Autowired
  public AdminApiImpl(AdminService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<EnginesDetailResponseModel> ping(Optional<Integer> engineId) {
    LOGGER.info("[Admin] ping request received for: {}", engineId.isEmpty() ? "all engines" : format("engine '%s'", engineId));
    if (engineId.isEmpty()) {
      List<EngineDetailResponse> engineDetailResponses = service.pingAll();
      return ResponseEntity.ok(EngineDetailResponseMapper.toServiceModel(engineDetailResponses.toArray(new EngineDetailResponse[0])));
    } else {
      EngineDetailResponse engineDetailResponse = service.ping(engineId.get());
      return ResponseEntity.ok(EngineDetailResponseMapper.toServiceModel(engineDetailResponse));
    }
  }
}
