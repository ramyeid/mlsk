package org.mlsk.lib.model;

import java.util.Objects;

import static java.lang.String.format;

public class Endpoint {

  private final String host;
  private final Long port;

  public Endpoint(String host, Long port) {
    this.host = host;
    this.port = port;
  }

  public String getUrl() {
    return format("http://%s:%s/", host, port);
  }

  public String getHost() {
    return host;
  }

  public Long getPort() {
    return port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Endpoint that = (Endpoint) o;
    return Objects.equals(host, that.host) &&
        Objects.equals(port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(host, port);
  }

  @Override
  public String toString() {
    return "Endpoint{" +
        "host='" + host + '\'' +
        ", port='" + port + '\'' +
        '}';
  }
}
