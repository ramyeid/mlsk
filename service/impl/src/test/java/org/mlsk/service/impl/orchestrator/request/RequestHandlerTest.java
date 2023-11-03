package org.mlsk.service.impl.orchestrator.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.impl.orchestrator.request.model.Request;
import org.mlsk.service.impl.orchestrator.request.registry.RequestRegistry;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.lang.String.valueOf;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestHandlerTest {

  private static final String ACTION = "Action";
  private static final Endpoint ENDPOINT = new Endpoint("host", 123L);

  @Mock
  private RequestRegistry requestRegistry;

  private RequestHandler requestHandler;

  @BeforeEach
  void setUp() {
    requestHandler = new RequestHandler(requestRegistry);
  }

  @Test
  void should_add_new_request_to_registry_on_register() {

    requestHandler.registerNewRequest(ACTION, ENDPOINT);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).addRequest(valueOf(ENDPOINT.hashCode()), buildRequest());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_request_id_on_register() {

    String actualId = requestHandler.registerNewRequest(ACTION, ENDPOINT);

    assertEquals(valueOf(ENDPOINT.hashCode()), actualId);
  }

  @Test
  public void should_get_request_from_registry() {
    String requestId = "requestId";
    onGetRequestReturn(requestId, buildRequest());

    requestHandler.getRequest(requestId);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).getRequest(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_retrieve_correct_request() {
    String requestId = "requestId";
    onGetRequestReturn(requestId, buildRequest());

    Optional<Request> actualRequest = requestHandler.getRequest(requestId);

    assertTrue(actualRequest.isPresent());
    assertEquals(buildRequest(), actualRequest.get());
  }

  @Test
  public void should_remove_request_from_registry() {
    String requestId = "requestId";

    requestHandler.removeRequest(requestId);

    InOrder inOrder = buildInOrder();
    inOrder.verify(requestRegistry).removeRequest(requestId);
    inOrder.verifyNoMoreInteractions();
  }

  private InOrder buildInOrder() {
    return inOrder(requestRegistry);
  }

  private void onGetRequestReturn(String requestId, Request request) {
    when(requestRegistry.getRequest(requestId)).thenReturn(ofNullable(request));
  }

  private static Request buildRequest() {
    return new Request(ACTION, ENDPOINT);
  }
}