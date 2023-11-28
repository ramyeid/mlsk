package org.mlsk.service.impl.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.service.engine.Engine;
import org.mlsk.service.impl.admin.service.exception.AdminServiceException;
import org.mlsk.service.impl.orchestrator.Orchestrator;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mlsk.service.model.admin.ProcessDetailResponse;
import org.mlsk.service.model.admin.RequestDetailResponse;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mlsk.service.impl.testhelper.OrchestratorHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

  @Mock
  private Orchestrator orchestrator;
  @Mock
  private Engine engine;

  private AdminServiceImpl service;

  @BeforeEach
  public void setUp() {
    this.service = new AdminServiceImpl(orchestrator);
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_ping() {
    onPriorityRunOnEngine(orchestrator, engine, 1);
    onPingReturn(buildEngineDetailResult());

    service.ping(1);

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).priorityRunOnEngine(eq(1), eq("ping"), any());
    inOrder.verify(engine).ping();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_ping() {
    onPriorityRunOnEngine(orchestrator, engine, 1);
    onPingReturn(buildEngineDetailResult());

    EngineDetailResponse actualEngineDetail = service.ping(1);

    assertEquals(buildEngineDetailResult(), actualEngineDetail);
  }

  @Test
  public void should_throw_admin_service_exception_on_ping_failure() {
    doThrowExceptionOnPriorityRunOnEngine(orchestrator, engine, "ping", 1, "exception message");

    try {
      service.ping(1);
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnAdminServiceException(exception, "exception message");
    }
  }

  @Test
  public void should_delegate_call_to_orchestrator_and_engine_on_ping_all() {
    onPriorityRunOnAllEngines(orchestrator, newArrayList(engine));
    onPingReturn(buildEngineDetailResult());

    service.pingAll();

    InOrder inOrder = buildInOrder();
    inOrder.verify(orchestrator).priorityRunOnAllEngines(eq("ping"), any());
    inOrder.verify(engine).ping();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_result_on_ping_all() {
    onPriorityRunOnAllEngines(orchestrator, newArrayList(engine));
    onPingReturn(buildEngineDetailResult());

    List<EngineDetailResponse> actualEngineDetail = service.pingAll();

    assertEquals(newArrayList(buildEngineDetailResult()), actualEngineDetail);
  }

  @Test
  public void should_throw_admin_service_exception_on_ping_all_failure() {
    doThrowExceptionOnPriorityRunOnAllEngines(orchestrator, engine, "ping", "exception message");

    try {
      service.pingAll();
      fail("should throw exception");

    } catch (Exception exception) {
      assertOnAdminServiceException(exception, "exception message");
    }
  }

  private InOrder buildInOrder() {
    return inOrder(orchestrator, engine);
  }

  private void onPingReturn(EngineDetailResponse engineDetailResponse) {
    when(engine.ping()).thenReturn(engineDetailResponse);
  }

  private static EngineDetailResponse buildEngineDetailResult() {
    return new EngineDetailResponse(
        newArrayList(new ProcessDetailResponse(1, "state", 2L, "startDatetime")),
        newArrayList(new RequestDetailResponse(1L, "type", "creationDatetime"))
    );
  }

  private static void assertOnAdminServiceException(Exception exception, String exceptionMessage) {
    assertInstanceOf(AdminServiceException.class, exception);
    assertEquals(exceptionMessage, exception.getMessage());
  }
}