package org.mlsk.service.impl.admin.api.mapper;

import org.mlsk.api.service.admin.model.ProcessDetailResponseModel;
import org.mlsk.service.model.admin.ProcessDetailResponse;

public final class ProcessDetailResponseMapper {

  private ProcessDetailResponseMapper() {
  }

  public static ProcessDetailResponseModel toServiceModel(ProcessDetailResponse processDetailResponse) {
    return new ProcessDetailResponseModel(
        processDetailResponse.getId(),
        processDetailResponse.getState(),
        processDetailResponse.getFlipFlopCount(),
        processDetailResponse.getStartDatetime()
    );
  }
}
