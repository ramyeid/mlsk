package org.mlsk.service.model.admin;

import java.util.List;
import java.util.Objects;

public class EngineDetailResponse {

  private final List<ProcessDetailResponse> processDetails;
  private final List<RequestDetailResponse> inflightRequestsDetails;

  public EngineDetailResponse(List<ProcessDetailResponse> processDetails, List<RequestDetailResponse> inflightRequestsDetails) {
    this.processDetails = processDetails;
    this.inflightRequestsDetails = inflightRequestsDetails;
  }

  public List<ProcessDetailResponse> getProcessDetails() {
    return processDetails;
  }

  public List<RequestDetailResponse> getInflightRequestsDetails() {
    return inflightRequestsDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EngineDetailResponse that = (EngineDetailResponse) o;
    return Objects.equals(processDetails, that.processDetails) && Objects.equals(inflightRequestsDetails, that.inflightRequestsDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processDetails, inflightRequestsDetails);
  }

  @Override
  public String toString() {
    return "EngineDetailResponse{" +
        "processDetails=" + processDetails +
        ", inflightRequestsDetails=" + inflightRequestsDetails +
        '}';
  }
}
