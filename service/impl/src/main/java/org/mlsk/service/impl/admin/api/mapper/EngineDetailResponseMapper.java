package org.mlsk.service.impl.admin.api.mapper;

import org.mlsk.api.service.admin.model.EngineDetailResponseModel;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.api.service.admin.model.ProcessDetailResponseModel;
import org.mlsk.api.service.admin.model.RequestDetailResponseModel;
import org.mlsk.service.model.admin.EngineDetailResponse;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class EngineDetailResponseMapper {

  private EngineDetailResponseMapper() {
  }

  public static EnginesDetailResponseModel toServiceModel(EngineDetailResponse... engineDetailResponses) {
    return new EnginesDetailResponseModel(
        Arrays.stream(engineDetailResponses)
            .map(engineDetailResponse -> {
              List<ProcessDetailResponseModel> processesDetailResponseModel = engineDetailResponse.getProcessDetails().stream().map(ProcessDetailResponseMapper::toServiceModel).collect(toList());
              List<RequestDetailResponseModel> requestsDetailResponseModel = engineDetailResponse.getInflightRequestsDetails().stream().map(RequestDetailResponseMapper::toServiceModel).collect(toList());
              return new EngineDetailResponseModel(processesDetailResponseModel, requestsDetailResponseModel);
            }).collect(toList())
    );
  }
}
