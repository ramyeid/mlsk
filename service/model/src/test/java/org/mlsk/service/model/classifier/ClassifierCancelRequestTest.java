package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierCancelRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierCancelRequest classifierCancelRequest = new ClassifierCancelRequest(1L);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedDataResponse = objectMapper.writeValueAsString(classifierCancelRequest);
    ClassifierCancelRequest deserializedClassifierCancelRequest = objectMapper.readValue(serializedDataResponse, ClassifierCancelRequest.class);

    assertEquals(deserializedClassifierCancelRequest, classifierCancelRequest);
  }
}