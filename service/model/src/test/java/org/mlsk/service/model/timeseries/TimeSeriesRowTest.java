package org.mlsk.service.model.timeseries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeSeriesRowTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    TimeSeriesRow row = new TimeSeriesRow("date1", 123.124);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedTimeSeriesRow = objectMapper.writeValueAsString(row);
    TimeSeriesRow deserializedRow = objectMapper.readValue(serializedTimeSeriesRow, TimeSeriesRow.class);

    assertEquals(deserializedRow, row);
  }
}
