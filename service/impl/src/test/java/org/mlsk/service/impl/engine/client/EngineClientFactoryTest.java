package org.mlsk.service.impl.engine.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.ServiceInformation;
import org.mlsk.service.impl.timeseries.engine.TimeSeriesAnalysisEngineClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EngineClientFactoryTest {

  private EngineClientFactory engineClientFactory;

  @BeforeEach
  public void setUp() {
    engineClientFactory = new EngineClientFactory();
  }

  @Test
  public void should_build_time_series_analysis_engine_client() {
    ServiceInformation serviceInformation = buildServiceInformation();

    TimeSeriesAnalysisEngineClient actualClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(serviceInformation);

    assertNotNull(actualClient);
    assertInstanceOf(TimeSeriesAnalysisEngineClient.class, actualClient);
  }

  private static ServiceInformation buildServiceInformation() {
    return new ServiceInformation("host", 495L);
  }

}