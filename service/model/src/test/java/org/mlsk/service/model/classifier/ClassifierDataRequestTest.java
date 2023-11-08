package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierDataRequestTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierDataRequest classifierDataRequest = new ClassifierDataRequest(123, "columnName", newArrayList(1, 2, 3), ClassifierType.DECISION_TREE);
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedDataRequest = objectMapper.writeValueAsString(classifierDataRequest);
    ClassifierDataRequest deserializedClassifierDataRequest = objectMapper.readValue(serializedDataRequest, ClassifierDataRequest.class);

    assertEquals(deserializedClassifierDataRequest, classifierDataRequest);
  }
}