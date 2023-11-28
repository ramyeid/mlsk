package org.mlsk.service.impl.admin.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlsk.api.service.admin.model.EngineDetailResponseModel;
import org.mlsk.api.service.admin.model.EnginesDetailResponseModel;
import org.mlsk.api.service.admin.model.ProcessDetailResponseModel;
import org.mlsk.api.service.admin.model.RequestDetailResponseModel;
import org.mlsk.service.admin.AdminService;
import org.mlsk.service.model.admin.EngineDetailResponse;
import org.mlsk.service.model.admin.ProcessDetailResponse;
import org.mlsk.service.model.admin.RequestDetailResponse;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.mlsk.service.impl.testhelper.ResponseEntityHelper.assertOnResponseEntity;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminApiImplTest {

  @Mock
  private AdminService service;

  private AdminApiImpl adminApi;

  @BeforeEach
  public void setUp() {
    this.adminApi = new AdminApiImpl(service);
  }

  @Test
  public void should_delegate_call_to_service_on_ping_with_id() {
    onServicePingReturn(10, buildEngineDetailResponse1());

    adminApi.ping(of(10));

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).ping(10);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_ping_with_id() {
    onServicePingReturn(10, buildEngineDetailResponse1());

    ResponseEntity<EnginesDetailResponseModel> actualResponse = adminApi.ping(of(10));

    EngineDetailResponseModel expectedResponse1 = buildEngineDetailResponseResult1();
    assertOnResponseEntity(buildEnginesDetailResponseResult(expectedResponse1), actualResponse);
  }

  @Test
  public void should_delegate_call_to_service_on_ping_all() {
    onServicePingAllReturn(newArrayList(buildEngineDetailResponse1(), buildEngineDetailResponse2()));

    adminApi.ping(empty());

    InOrder inOrder = buildInOrder();
    inOrder.verify(service).pingAll();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void should_return_correct_response_on_ping_all() {
    onServicePingAllReturn(newArrayList(buildEngineDetailResponse1(), buildEngineDetailResponse2()));

    ResponseEntity<EnginesDetailResponseModel> actualResponse = adminApi.ping(empty());

    EngineDetailResponseModel expectedResponse1 = buildEngineDetailResponseResult1();
    EngineDetailResponseModel expectedResponse2 = buildEngineDetailResponseResult2();
    assertOnResponseEntity(buildEnginesDetailResponseResult(expectedResponse1, expectedResponse2), actualResponse);
  }

  private InOrder buildInOrder() {
    return inOrder(service);
  }

  private void onServicePingAllReturn(List<EngineDetailResponse> engineDetailResponses) {
    when(service.pingAll()).thenReturn(engineDetailResponses);
  }

  private void onServicePingReturn(int engineId, EngineDetailResponse engineDetailResponse) {
    when(service.ping(engineId)).thenReturn(engineDetailResponse);
  }

  private EnginesDetailResponseModel buildEnginesDetailResponseResult(EngineDetailResponseModel... expectedResponses) {
    return new EnginesDetailResponseModel(
        Arrays.stream(expectedResponses).collect(toList())
    );
  }

  private static EngineDetailResponseModel buildEngineDetailResponseResult1() {
    return new EngineDetailResponseModel(
        newArrayList(
            new ProcessDetailResponseModel(1, "state", 2L, "startDatetime")
        ),
        newArrayList(
            new RequestDetailResponseModel(2L, "type", "creationDatetime")
        )
    );
  }

  private static EngineDetailResponseModel buildEngineDetailResponseResult2() {
    return new EngineDetailResponseModel(
        newArrayList(
            new ProcessDetailResponseModel(3, "state3", 3L, "startDatetime3")
        ),
        newArrayList(
            new RequestDetailResponseModel(4L, "type4", "creationDatetime4")
        )
    );
  }

  private static EngineDetailResponse buildEngineDetailResponse1() {
    return new EngineDetailResponse(
        newArrayList(
            new ProcessDetailResponse(1, "state", 2L, "startDatetime")
        ),
        newArrayList(
            new RequestDetailResponse(2L, "type", "creationDatetime")
        )
    );
  }

  private static EngineDetailResponse buildEngineDetailResponse2() {
    return new EngineDetailResponse(
        newArrayList(
            new ProcessDetailResponse(3, "state3", 3L, "startDatetime3")
        ),
        newArrayList(
            new RequestDetailResponse(4L, "type4", "creationDatetime4")
        )
    );
  }
}