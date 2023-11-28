package org.mlsk.service.model.admin;

import java.util.Objects;

public class RequestDetailResponse {

  private final long id;
  private final String type;
  private final String creationDatetime;

  public RequestDetailResponse(long id, String type, String creationDatetime) {
    this.id = id;
    this.type = type;
    this.creationDatetime = creationDatetime;
  }

  public long getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getCreationDatetime() {
    return creationDatetime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RequestDetailResponse that = (RequestDetailResponse) o;
    return id == that.id && Objects.equals(type, that.type) && Objects.equals(creationDatetime, that.creationDatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, creationDatetime);
  }

  @Override
  public String toString() {
    return "RequestDetailResponse{" +
        "id=" + id +
        ", type='" + type + '\'' +
        ", creationDatetime='" + creationDatetime + '\'' +
        '}';
  }
}
