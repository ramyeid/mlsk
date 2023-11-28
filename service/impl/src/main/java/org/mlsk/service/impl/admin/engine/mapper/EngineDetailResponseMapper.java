package org.mlsk.service.impl.admin.engine.mapper;

import org.mlsk.api.engine.admin.model.EngineDetailResponseModel;
import org.mlsk.service.model.admin.EngineDetailResponse;

import static java.util.stream.Collectors.toList;

public final class EngineDetailResponseMapper {

  private EngineDetailResponseMapper() {
  }

  public static EngineDetailResponse fromEngineModel(EngineDetailResponseModel engineDetailResponseModel) {
    return new EngineDetailResponse(
        engineDetailResponseModel.getProcessesDetails().stream().map(ProcessDetailResponseMapper::fromEngineModel).collect(toList()),
        engineDetailResponseModel.getInflightRequestsDetails().stream().map(RequestDetailResponseMapper::fromEngineModel).collect(toList())
    );
  }
}
