package org.mlsk.service.impl.admin.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.engine.admin.client.AdminEngineApi;
import org.mlsk.api.engine.admin.model.EngineDetailResponseModel;
import org.mlsk.service.impl.admin.engine.exception.AdminEngineRequestException;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.security.InvalidParameterException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminEngineClientTest {

  @Mock
  private AdminEngineApi adminEngineApi;

  private AdminEngineClient client;

  @BeforeEach
  public void setUp() {
    this.client = new AdminEngineClient(adminEngineApi);
  }

  @Test
  public void should_delegate_ping_call_to_engine() {
    when(adminEngineApi.ping()).thenReturn(buildEngineDetailResponseModel());

    EngineDetailResponse actualResponse = client.ping();

    verify(adminEngineApi).ping();
    assertEquals(buildEngineDetailResponse(), actualResponse);
  }

  @Test
  public void should_rethrow_admin_exception_on_ping_failure() {
    doThrow(new InvalidParameterException()).when(adminEngineApi).ping();

    try {
      client.ping();
      fail("should fail");

    } catch (Exception exception) {
      assertOnAdminEngineRequestException(exception, "Failed to call ping to engine");
    }
  }

  @Test
  public void should_throw_exception_with_body_on_forecast_failure_with_http_server_error_exception() {
    doThrow(buildHttpServerErrorException("Original Forecast Exception Message")).when(adminEngineApi).ping();

    try {
      client.ping();
      fail("should fail");

    } catch (Exception exception) {
      assertOnAdminEngineRequestExceptionWithServerError(exception, "Failed to call ping to engine: Original Forecast Exception Message");
    }
  }

  private static EngineDetailResponseModel buildEngineDetailResponseModel() {
    return new EngineDetailResponseModel(
        newArrayList(),
        newArrayList()
    );
  }

  private static EngineDetailResponse buildEngineDetailResponse() {
    return new EngineDetailResponse(
        newArrayList(),
        newArrayList()
    );
  }

  private static HttpServerErrorException buildHttpServerErrorException(String exceptionMessage) {
    return new HttpServerErrorException("message", HttpStatus.INTERNAL_SERVER_ERROR, "status", null, exceptionMessage.getBytes(), null);
  }

  private static void assertOnAdminEngineRequestException(Exception exception, String exceptionMessage) {
    assertInstanceOf(AdminEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(InvalidParameterException.class, exception.getCause());
  }

  private static void assertOnAdminEngineRequestExceptionWithServerError(Exception exception, String exceptionMessage) {
    assertInstanceOf(AdminEngineRequestException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
    assertInstanceOf(HttpServerErrorException.class, exception.getCause());
  }
}