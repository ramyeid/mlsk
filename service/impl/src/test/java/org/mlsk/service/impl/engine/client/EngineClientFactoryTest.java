package org.mlsk.service.impl.engine.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.classifier.engine.ClassifierEngineClient;
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
    Endpoint endpoint = buildEndpoint();

    TimeSeriesAnalysisEngineClient actualClient = engineClientFactory.buildTimeSeriesAnalysisEngineClient(endpoint);

    assertNotNull(actualClient);
    assertInstanceOf(TimeSeriesAnalysisEngineClient.class, actualClient);
  }

  @Test
  public void should_build_classifier_engine_client() {
    Endpoint endpoint = buildEndpoint();

    ClassifierEngineClient actualClient = engineClientFactory.buildClassifierEngineClient(endpoint);

    assertNotNull(actualClient);
    assertInstanceOf(ClassifierEngineClient.class, actualClient);
  }

  private static Endpoint buildEndpoint() {
    return new Endpoint("host", 495L);
  }

}