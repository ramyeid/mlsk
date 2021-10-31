package org.mlsk.service.impl.orchestrator.request;

import org.mlsk.lib.model.ServiceInformation;

import java.time.Instant;
import java.util.Objects;

import static java.lang.String.valueOf;

public class RequestIdGenerator {

  public String generateRequestId(ServiceInformation serviceInformation) {
    long currentTime = Instant.now().toEpochMilli();
    String engineHost = serviceInformation.getHost();
    Long enginePort = serviceInformation.getPort();

    return valueOf(Objects.hash(currentTime, engineHost, enginePort));
  }
}
