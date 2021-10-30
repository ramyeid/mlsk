package org.mlsk.service.model.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassifierDataResponseTest {

  @Test
  public void should_be_able_to_deserialize_and_serialize_to_json() throws JsonProcessingException {
    ClassifierDataResponse classifierDataResponse = new ClassifierDataResponse("columnName", newArrayList(1, 2, 3));
    ObjectMapper objectMapper = new ObjectMapper();

    String serializedDataResponse = objectMapper.writeValueAsString(classifierDataResponse);
    ClassifierDataResponse deserializedClassifierDataResponse = objectMapper.readValue(serializedDataResponse, ClassifierDataResponse.class);

    assertEquals(deserializedClassifierDataResponse, classifierDataResponse);
  }
}