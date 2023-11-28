package org.mlsk.service.impl.admin.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.admin.model.ProcessDetailResponseModel;
import org.mlsk.service.model.admin.ProcessDetailResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.api.mapper.ProcessDetailResponseMapper.toServiceModel;

public class ProcessDetailResponseMapperTest {

  @Test
  public void should_correctly_map_to_process_detail_response_model() {
    ProcessDetailResponse processDetailResponse = buildProcessDetailResponse();

    ProcessDetailResponseModel actualProcessDetailResponseModel = toServiceModel(processDetailResponse);

    assertEquals(buildExpectedProcessDetailResponseModel(), actualProcessDetailResponseModel);
  }

  private static ProcessDetailResponseModel buildExpectedProcessDetailResponseModel() {
    return new ProcessDetailResponseModel(
        10,
        "state",
        30L,
        "startDatetime"
    );
  }

  private static ProcessDetailResponse buildProcessDetailResponse() {
    return new ProcessDetailResponse(
        10,
        "state",
        30L,
        "startDatetime"
    );
  }
}