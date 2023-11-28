package org.mlsk.service.model.admin;

import java.util.Objects;

public class ProcessDetailResponse {

  private final int id;
  private final String state;
  private final long flipFlopCount;
  private final String startDatetime;

  public ProcessDetailResponse(int id, String state, long flipFlopCount, String startDatetime) {
    this.id = id;
    this.state = state;
    this.flipFlopCount = flipFlopCount;
    this.startDatetime = startDatetime;
  }

  public int getId() {
    return id;
  }

  public String getState() {
    return state;
  }

  public long getFlipFlopCount() {
    return flipFlopCount;
  }

  public String getStartDatetime() {
    return startDatetime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProcessDetailResponse that = (ProcessDetailResponse) o;
    return id == that.id && flipFlopCount == that.flipFlopCount && Objects.equals(state, that.state) && Objects.equals(startDatetime, that.startDatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state, flipFlopCount, startDatetime);
  }

  @Override
  public String toString() {
    return "ProcessDetailResponse{" +
        "id=" + id +
        ", state='" + state + '\'' +
        ", flipFlopCount=" + flipFlopCount +
        ", startDatetime='" + startDatetime + '\'' +
        '}';
  }
}
