package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierDropRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierDropRequest classifierDropRequest = new ClassifierDropRequest(1L);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedDataResponse = objectMapper.writeValueAsString(classifierDropRequest);
    ClassifierDropRequest deserializedClassifierDropRequest = objectMapper.readValue(serializedDataResponse, ClassifierDropRequest.class);

    assertEquals(deserializedClassifierDropRequest, classifierDropRequest);
  }
}