package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierRequest classifierRequest = new ClassifierRequest("requestId");
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedRequest = objectMapper.writeValueAsString(classifierRequest);
    ClassifierRequest deserializedClassifierRequest = objectMapper.readValue(serializedRequest, ClassifierRequest.class);

    assertEquals(deserializedClassifierRequest, classifierRequest);
  }
}
