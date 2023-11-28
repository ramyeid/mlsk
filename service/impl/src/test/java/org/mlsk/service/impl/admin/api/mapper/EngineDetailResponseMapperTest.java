package org.mlsk.service.impl.admin.api.mapper;

import org.junit.jupiter.api.Test;
import org.mlsk.api.service.admin.model.EngineDetailResponseModel;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.api.service.admin.model.ProcessDetailResponseModel;
import org.mlsk.api.service.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mlsk.service.model.admin.ProcessDetailResponse;
import org.mlsk.service.model.admin.RequestDetailResponse;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.impl.admin.api.mapper.EngineDetailResponseMapper.toServiceModel;

public class EngineDetailResponseMapperTest {

  @Test
  public void should_correctly_map_1_engine_detail_to_engines_detail_response_model() {
    EngineDetailResponse engineDetailResponse = buildEngineDetailResponse1();

    EnginesDetailResponseModel actualEngineDetailResponseModel = toServiceModel(engineDetailResponse);

    EngineDetailResponseModel expectedResponseModel = buildExpectedEngineDetailResponseModel1();
    assertEquals(buildExpectedEnginesDetailResponseModel(expectedResponseModel), actualEngineDetailResponseModel);
  }

  @Test
  public void should_correctly_map_multiple_engine_detail_to_engines_detail_response_model() {
    EngineDetailResponse engineDetailResponse1 = buildEngineDetailResponse1();
    EngineDetailResponse engineDetailResponse2 = buildEngineDetailResponse2();

    EnginesDetailResponseModel actualEngineDetailResponseModel = toServiceModel(engineDetailResponse1, engineDetailResponse2);

    EngineDetailResponseModel expectedResponseModel1 = buildExpectedEngineDetailResponseModel1();
    EngineDetailResponseModel expectedResponseModel2 = buildExpectedEngineDetailResponseModel2();
    assertEquals(buildExpectedEnginesDetailResponseModel(expectedResponseModel1, expectedResponseModel2), actualEngineDetailResponseModel);
  }

  private static EnginesDetailResponseModel buildExpectedEnginesDetailResponseModel(EngineDetailResponseModel... engineDetailResponseModels) {
    return new EnginesDetailResponseModel(
        Arrays.stream(engineDetailResponseModels).collect(Collectors.toList())
    );
  }

  private static EngineDetailResponseModel buildExpectedEngineDetailResponseModel1() {
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

  private static EngineDetailResponseModel buildExpectedEngineDetailResponseModel2() {
    return new EngineDetailResponseModel(
        newArrayList(
            new ProcessDetailResponseModel(6, "state6", 6L, "startDatetime6"),
            new ProcessDetailResponseModel(7, "state7", 7L, "startDatetime7")
        ),
        newArrayList(
            new RequestDetailResponseModel(8L, "type8", "creationDatetime8"),
            new RequestDetailResponseModel(9L, "type9", "creationDatetime9"),
            new RequestDetailResponseModel(10L, "type10", "creationDatetime10")
        )
    );
  }

  private static EngineDetailResponse buildEngineDetailResponse1() {
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

  private static EngineDetailResponse buildEngineDetailResponse2() {
    return new EngineDetailResponse(
        newArrayList(
            new ProcessDetailResponse(6, "state6", 6L, "startDatetime6"),
            new ProcessDetailResponse(7, "state7", 7L, "startDatetime7")
        ),
        newArrayList(
            new RequestDetailResponse(8L, "type8", "creationDatetime8"),
            new RequestDetailResponse(9L, "type9", "creationDatetime9"),
            new RequestDetailResponse(10L, "type10", "creationDatetime10")
        )
    );
  }
}