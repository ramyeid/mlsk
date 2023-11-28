package org.mlsk.service.impl.admin.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.RequestDetailResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.api.mapper.RequestDetailResponseMapper.toServiceModel;

public class RequestDetailResponseMapperTest {

  @Test
  public void should_correctly_map_to_request_detail_response_model() {
    RequestDetailResponse requestDetailResponse = buildRequestDetailResponse();

    org.mlsk.api.service.admin.model.RequestDetailResponseModel actualRequestDetailResponse = toServiceModel(requestDetailResponse);

    assertEquals(buildExpectedRequestDetailResponseModel(), actualRequestDetailResponse);
  }

  private static RequestDetailResponseModel buildExpectedRequestDetailResponseModel() {
    return new RequestDetailResponseModel(
        5L,
        "type",
        "creationDatetime"
    );
  }

  private static RequestDetailResponse buildRequestDetailResponse() {
    return new RequestDetailResponse(
        5L,
        "type",
        "creationDatetime"
    );
  }
}