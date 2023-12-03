package org.mlsk.service.impl.admin.engine.mapper;

import org.mlsk.api.engine.admin.model.ProcessDetailResponseModel;
import org.mlsk.service.model.admin.ProcessDetailResponse;

public final class ProcessDetailResponseMapper {

  private ProcessDetailResponseMapper() {
  }


  public static ProcessDetailResponse fromEngineModel(ProcessDetailResponseModel processDetailResponseModel) {
    return new ProcessDetailResponse(
        processDetailResponseModel.getId().intValue(),
        processDetailResponseModel.getState(),
        processDetailResponseModel.getFlipFlopCount().intValue(),
        processDetailResponseModel.getStartDatetime()
    );
  }
}
