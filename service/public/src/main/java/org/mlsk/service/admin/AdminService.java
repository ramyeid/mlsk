package org.mlsk.service.admin;

import org.mlsk.service.model.admin.EngineDetailResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AdminService {

  EngineDetailResponse ping(int engineId);

  List<EngineDetailResponse> pingAll();
}
