package org.mlsk.service.impl.admin.engine.mapper;

import org.mlsk.api.engine.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.RequestDetailResponse;

public final class RequestDetailResponseMapper {

  private RequestDetailResponseMapper() {
  }

  public static RequestDetailResponse fromEngineModel(RequestDetailResponseModel requestDetailResponseModel) {
    return new RequestDetailResponse(
        requestDetailResponseModel.getId(),
        requestDetailResponseModel.getType(),
        requestDetailResponseModel.getCreationDatetime()
    );
  }
}
