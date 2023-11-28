package org.mlsk.service.impl.admin.engine.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.engine.admin.model.EngineDetailResponseModel;
import org.mlsk.api.engine.admin.model.ProcessDetailResponseModel;
import org.mlsk.api.engine.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mlsk.service.model.admin.ProcessDetailResponse;
import org.mlsk.service.model.admin.RequestDetailResponse;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.engine.mapper.EngineDetailResponseMapper.fromEngineModel;

public class EngineDetailResponseMapperTest {

  @Test
  public void should_correctly_map_to_engine_detail_response() {
    EngineDetailResponseModel engineDetailResponseModel = buildEngineDetailResponseModel();

    EngineDetailResponse actualEngineDetailResponse = fromEngineModel(engineDetailResponseModel);

    assertEquals(buildExpectedEngineDetailResponse(), actualEngineDetailResponse);
  }

  private static EngineDetailResponseModel buildEngineDetailResponseModel() {
    return new EngineDetailResponseModel(
        newArrayList(
            new ProcessDetailResponseModel(1, "state1", 1L, "startDatetime1"),
            new ProcessDetailResponseModel(2, "state2", 2L, "startDatetime2")
        ),
        newArrayList(
            new RequestDetailResponseModel(3L, "type3", "creationDatetime3"),
            new RequestDetailResponseModel(4L, "type4", "creationDatetime4"),
            new RequestDetailResponseModel(5L, "type5", "creationDatetime5")
        )
    );
  }

  private static EngineDetailResponse buildExpectedEngineDetailResponse() {
    return new EngineDetailResponse(
        newArrayList(
            new ProcessDetailResponse(1, "state1", 1L, "startDatetime1"),
            new ProcessDetailResponse(2, "state2", 2L, "startDatetime2")
        ),
        newArrayList(
            new RequestDetailResponse(3L, "type3", "creationDatetime3"),
            new RequestDetailResponse(4L, "type4", "creationDatetime4"),
            new RequestDetailResponse(5L, "type5", "creationDatetime5")
        )
    );
  }
}