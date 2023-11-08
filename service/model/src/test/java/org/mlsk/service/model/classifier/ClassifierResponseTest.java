package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierResponseTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierResponse classifierResponse = new ClassifierResponse(1L, "columnName", newArrayList(1, 2, 3), ClassifierType.DECISION_TREE);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedResponse = objectMapper.writeValueAsString(classifierResponse);
    ClassifierResponse deserializedClassifierResponse = objectMapper.readValue(serializedResponse, ClassifierResponse.class);

    assertEquals(deserializedClassifierResponse, classifierResponse);
  }
}