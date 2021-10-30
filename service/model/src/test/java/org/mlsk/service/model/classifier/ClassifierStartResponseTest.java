package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierStartResponseTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierStartResponse classifierStartResponse = new ClassifierStartResponse("requestId");
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedStartResponse = objectMapper.writeValueAsString(classifierStartResponse);
    ClassifierStartResponse deserializedClassifierStartResponse = objectMapper.readValue(serializedStartResponse, ClassifierStartResponse.class);

    assertEquals(deserializedClassifierStartResponse, classifierStartResponse);
  }
}