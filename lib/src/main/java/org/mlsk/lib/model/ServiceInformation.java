package org.mlsk.lib.model;

import org.mlsk.lib.rest.IServiceInformation;

import java.util.Objects;

public class ServiceInformation implements IServiceInformation {

  private final String host;
  private final String port;

  public ServiceInformation(String host, String port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public String getUrl() {
    return String.format("http://%s:%s/", host, port);
  }

  @Override
  public String getPort() {
    return port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServiceInformation that = (ServiceInformation) o;
    return Objects.equals(host, that.host) &&
        Objects.equals(port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(host, port);
  }

  @Override
  public String toString() {
    return "ServiceInformation{" +
        "host='" + host + '\'' +
        ", port='" + port + '\'' +
        '}';
  }
}
