package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierStartRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierStartRequest classifierStartRequest = new ClassifierStartRequest(123, "predictionColumnName", newArrayList("col1", "col2"), 12, ClassifierType.DECISION_TREE);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedStartRequest = objectMapper.writeValueAsString(classifierStartRequest);
    ClassifierStartRequest deserializedClassifierStartRequest = objectMapper.readValue(serializedStartRequest, ClassifierStartRequest.class);

    assertEquals(deserializedClassifierStartRequest, classifierStartRequest);
  }
}