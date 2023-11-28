package org.mlsk.service.impl.admin.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.RequestDetailResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.engine.mapper.RequestDetailResponseMapper.fromEngineModel;

public class RequestDetailResponseMapperTest {

  @Test
  public void should_correctly_map_to_request_detail_response() {
    RequestDetailResponseModel requestDetailResponseModel = buildRequestDetailResponseModel();

    RequestDetailResponse actualRequestDetailResponse = fromEngineModel(requestDetailResponseModel);

    assertEquals(buildExpectedRequestDetailResponse(), actualRequestDetailResponse);
  }

  private static RequestDetailResponseModel buildRequestDetailResponseModel() {
    return new RequestDetailResponseModel(
        5L,
        "type",
        "creationDatetime"
    );
  }

  private static RequestDetailResponse buildExpectedRequestDetailResponse() {
    return new RequestDetailResponse(
        5L,
        "type",
        "creationDatetime"
    );
  }
}