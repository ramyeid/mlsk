package org.mlsk.service.impl.admin.engine;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.api.engine.admin.client.AdminEngineApi;
import org.mlsk.lib.model.Endpoint;
import org.mlsk.service.admin.AdminEngine;
import org.mlsk.service.impl.admin.engine.exception.AdminEngineRequestException;
import org.mlsk.service.impl.admin.engine.mapper.EngineDetailResponseMapper;
import org.mlsk.service.impl.engine.client.EngineClientFactory;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.springframework.web.client.HttpServerErrorException;

import static java.lang.String.format;

public class AdminEngineClient implements AdminEngine {

  private final AdminEngineApi adminClient;

  public AdminEngineClient(Endpoint endpoint, EngineClientFactory engineClientFactory) {
    this(engineClientFactory.buildAdminClient(endpoint));
  }

  @VisibleForTesting
  AdminEngineClient(AdminEngineApi adminClient) {
    this.adminClient = adminClient;
  }

  @Override
  public EngineDetailResponse ping() {
    try {
      return EngineDetailResponseMapper.fromEngineModel(adminClient.ping());
    } catch (HttpServerErrorException exception) {
      throw buildAdminEngineRequestException(exception, "ping");
    } catch (Exception exception) {
      throw buildAdminEngineRequestException(exception, "ping");
    }
  }

  private static AdminEngineRequestException buildAdminEngineRequestException(Exception exception, String action) {
    return new AdminEngineRequestException(format("Failed to call %s to engine", action), exception);
  }

  private static AdminEngineRequestException buildAdminEngineRequestException(HttpServerErrorException exception, String action) {
    return new AdminEngineRequestException(format("Failed on call %s to engine: %s", action, exception.getResponseBodyAsString()), exception);
  }
}
