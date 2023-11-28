package org.mlsk.service.impl.admin.api.mapper;

import org.mlsk.api.service.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.RequestDetailResponse;

public final class RequestDetailResponseMapper {

  private RequestDetailResponseMapper() {
  }

  public static RequestDetailResponseModel toServiceModel(RequestDetailResponse requestDetailResponse) {
    return new RequestDetailResponseModel(
        (long) requestDetailResponse.getId(),
        requestDetailResponse.getType(),
        requestDetailResponse.getCreationDatetime()
    );
  }
}
