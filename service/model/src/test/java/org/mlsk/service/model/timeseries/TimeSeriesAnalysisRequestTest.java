package org.mlsk.service.model.timeseries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.service.model.timeseries.TimeSeriesUtils.buildTimeSeriesPassengers;

public class TimeSeriesAnalysisRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(909, buildTimeSeriesPassengers(), 19);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedTimeSeries = objectMapper.writeValueAsString(timeSeriesAnalysisRequest);
    TimeSeriesAnalysisRequest deserializedTimeSeriesAnalysisRequest = objectMapper.readValue(serializedTimeSeries, TimeSeriesAnalysisRequest.class);

    assertEquals(deserializedTimeSeriesAnalysisRequest, timeSeriesAnalysisRequest);
  }
}