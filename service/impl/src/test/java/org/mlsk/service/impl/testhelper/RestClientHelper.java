package org.mlsk.service.impl.testhelper;

import org.mlsk.lib.rest.RestClient;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public final class RestClientHelper {

  private RestClientHelper() {
  }

  public static <Response> void onPostWithBodyWithResponseReturn(RestClient restClient, String resource, Object body, Class<Response> responseType, Response response) {
    when(restClient.post(resource, body, responseType)).thenReturn(response);
  }

  public static <Response> void onPostWithoutBodyWithResponseReturn(RestClient restClient, String resource, Class<Response> responseType, Response response) {
    when(restClient.post(resource, responseType)).thenReturn(response);
  }

  public static <Response> void doThrowExceptionOnPostWithBodyWithResponse(RestClient restClient, String resource, Object body, Class<Response> responseType, Exception exception) {
    when(restClient.post(resource, body, responseType)).thenThrow(exception);
  }

  public static void doThrowExceptionOnPostWithBodyWithoutResponse(RestClient restClient, String resource, Object body, Exception exception) {
    when(restClient.post(resource, body)).thenThrow(exception);
  }

  public static <Response> void doThrowExceptionOnPostWithoutBodyWithResponse(RestClient restClient, String resource, Class<Response> responseType, Exception exception) {
    when(restClient.post(resource, responseType)).thenThrow(exception);
  }

  public static void doThrowExceptionOnPostWithoutBodyWithoutResponse(RestClient restClient, String resource, Exception exception) {
    when(restClient.post(resource)).thenThrow(exception);
  }

  public static <Response> void verifyPostWithBodyWithResponseCalled(RestClient restClient, String resource, Object body, Class<Response> response) {
    InOrder inOrder = inOrder(restClient);
    inOrder.verify(restClient).post(resource, body, response);
    inOrder.verifyNoMoreInteractions();
  }

  public static void verifyPostWithBodyWithoutResponseCalled(RestClient restClient, String resource, Object body) {
    InOrder inOrder = inOrder(restClient);
    inOrder.verify(restClient).post(resource, body);
    inOrder.verifyNoMoreInteractions();
  }

  public static <Response> void verifyPostWithoutBodyWithResponseCalled(RestClient restClient, String resource, Class<Response> response) {
    InOrder inOrder = inOrder(restClient);
    inOrder.verify(restClient).post(resource, response);
    inOrder.verifyNoMoreInteractions();
  }

  public static void verifyPostWithoutBodyWithoutResponseCalled(RestClient restClient, String resource) {
    InOrder inOrder = inOrder(restClient);
    inOrder.verify(restClient).post(resource);
    inOrder.verifyNoMoreInteractions();
  }
}

