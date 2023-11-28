package org.mlsk.service.impl.admin.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.admin.model.ProcessDetailResponseModel;
import org.mlsk.service.model.admin.ProcessDetailResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.engine.mapper.ProcessDetailResponseMapper.fromEngineModel;

public class ProcessDetailResponseMapperTest {

  @Test
  public void should_correctly_map_to_process_detail_response() {
    ProcessDetailResponseModel processDetailResponseModel = buildProcessDetailResponseModel();

    ProcessDetailResponse actualProcessDetailResponse = fromEngineModel(processDetailResponseModel);

    assertEquals(buildExpectedProcessDetailResponse(), actualProcessDetailResponse);
  }

  private static ProcessDetailResponseModel buildProcessDetailResponseModel() {
    return new ProcessDetailResponseModel(
        10,
        "state",
        30L,
        "startDatetime"
    );
  }

  private static ProcessDetailResponse buildExpectedProcessDetailResponse() {
    return new ProcessDetailResponse(
        10,
        "state",
        30L,
        "startDatetime"
    );
  }
}