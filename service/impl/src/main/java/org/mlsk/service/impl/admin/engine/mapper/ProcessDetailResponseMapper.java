package org.mlsk.service.impl.admin.engine.mapper;

import org.mlsk.api.engine.admin.model.ProcessDetailResponseModel;
import org.mlsk.service.model.admin.ProcessDetailResponse;

public final class ProcessDetailResponseMapper {

  private ProcessDetailResponseMapper() {
  }


  public static ProcessDetailResponse fromEngineModel(ProcessDetailResponseModel processDetailResponseModel) {
    return new ProcessDetailResponse(
        processDetailResponseModel.getId(),
        processDetailResponseModel.getState(),
        processDetailResponseModel.getFlipFlopCount(),
        processDetailResponseModel.getStartDatetime()
    );
  }
}
