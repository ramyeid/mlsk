package org.mlsk.service.impl.inttest.admin.helper;

import org.mlsk.service.impl.admin.service.exception.AdminServiceException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class AdminHelper {

  private AdminHelper() {
  }

  public static org.mlsk.api.engine.admin.model.EngineDetailResponseModel buildEngine1DetailEngineResponse() {
    return new org.mlsk.api.engine.admin.model.EngineDetailResponseModel(
        newArrayList(
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(1, "IDLE", 1L, "1990"),
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(2, "BUSY", 5L, "1992"),
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(3, "IDLE", 2L, "1993")
        ),
        newArrayList(
            new org.mlsk.api.engine.admin.model.RequestDetailResponseModel(1L, "CLASSIFIER", "2000"),
            new org.mlsk.api.engine.admin.model.RequestDetailResponseModel(2L, "TIME_SERIES", "1000")
        )
    );
  }

  public static org.mlsk.api.engine.admin.model.EngineDetailResponseModel buildEngine2DetailEngineResponse() {
    return new org.mlsk.api.engine.admin.model.EngineDetailResponseModel(
        newArrayList(
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(1, "BUSY", 5L, "1991"),
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(2, "IDLE", 3L, "1995"),
            new org.mlsk.api.engine.admin.model.ProcessDetailResponseModel(3, "IDLE", 1L, "1996")
        ),
        newArrayList(
            new org.mlsk.api.engine.admin.model.RequestDetailResponseModel(10L, "CLASSIFIER", "2001"),
            new org.mlsk.api.engine.admin.model.RequestDetailResponseModel(20L, "TIME_SERIES", "1001")
        )
    );
  }

  public static org.mlsk.api.service.admin.model.EngineDetailResponseModel buildEngine1DetailServiceResponse() {
    return new org.mlsk.api.service.admin.model.EngineDetailResponseModel(
        newArrayList(
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(1, "IDLE", 1L, "1990"),
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(2, "BUSY", 5L, "1992"),
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(3, "IDLE", 2L, "1993")
        ),
        newArrayList(
            new org.mlsk.api.service.admin.model.RequestDetailResponseModel(1L, "CLASSIFIER", "2000"),
            new org.mlsk.api.service.admin.model.RequestDetailResponseModel(2L, "TIME_SERIES", "1000")
        )
    );
  }

  public static org.mlsk.api.service.admin.model.EngineDetailResponseModel buildEngine2DetailServiceResponse() {
    return new org.mlsk.api.service.admin.model.EngineDetailResponseModel(
        newArrayList(
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(1, "BUSY", 5L, "1991"),
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(2, "IDLE", 3L, "1995"),
            new org.mlsk.api.service.admin.model.ProcessDetailResponseModel(3, "IDLE", 1L, "1996")
        ),
        newArrayList(
            new org.mlsk.api.service.admin.model.RequestDetailResponseModel(10L, "CLASSIFIER", "2001"),
            new org.mlsk.api.service.admin.model.RequestDetailResponseModel(20L, "TIME_SERIES", "1001")
        )
    );
  }

  public static void assertOnAdminServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(AdminServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}
