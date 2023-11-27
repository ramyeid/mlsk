#!/usr/bin/python3

import unittest
from datetime import datetime
from engine_state import RequestType
from process_pool.process import ProcessState
from admin.model.request_detail_response import RequestDetailResponse
from admin.model.process_detail_response import ProcessDetailResponse
from admin.model.engine_detail_response import EngineDetailResponse


class TestEngineDetailResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response = EngineDetailResponse([process1, process2], [request1, request2])

    # When
    actual_inflight_requests = response.get_inflight_requests_details()
    actual_ongoing_processes = response.get_processes_details()

    # Then
    expected_inflight_requests = [
      RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10)),
      RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    ]
    expected_ongoing_processes = [
      ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10)),
      ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    ]
    self.assertEqual(expected_inflight_requests, actual_inflight_requests)
    self.assertEqual(expected_ongoing_processes, actual_ongoing_processes)


  def test_to_json(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response = EngineDetailResponse([process1, process2], [request1, request2])

    # When
    actual_json = response.to_json()

    # Then
    expected_json = dict(processesDetails=[process1, process2],
                         inflightRequestsDetails=[request1, request2])
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response = EngineDetailResponse([process1, process2], [request1, request2])

    # When
    actual_str = response.__str__()

    # Then
    expected_str = "{'processesDetails': [{'id': 1, 'state': 'BUSY', 'flipFlopCount': 10, 'startDatetime': '2000-10-10 10:10:10.000010'}, {'id': 2, 'state': 'IDLE', 'flipFlopCount': 2, 'startDatetime': '2001-10-10 10:10:10.000010'}], 'inflightRequestsDetails': [{'id': 1, 'type': 'CLASSIFIER', 'creationDatetime': '2000-10-10 10:10:10.000010'}, {'id': 2, 'type': 'TIME_SERIES_ANALYSIS', 'creationDatetime': '2001-10-10 10:10:10.000010'}]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response = EngineDetailResponse([process1, process2], [request1, request2])

    # When
    actual_str = response.__repr__()

    # Then
    expected_str = "{'processesDetails': [{'id': 1, 'state': 'BUSY', 'flipFlopCount': 10, 'startDatetime': '2000-10-10 10:10:10.000010'}, {'id': 2, 'state': 'IDLE', 'flipFlopCount': 2, 'startDatetime': '2001-10-10 10:10:10.000010'}], 'inflightRequestsDetails': [{'id': 1, 'type': 'CLASSIFIER', 'creationDatetime': '2000-10-10 10:10:10.000010'}, {'id': 2, 'type': 'TIME_SERIES_ANALYSIS', 'creationDatetime': '2001-10-10 10:10:10.000010'}]}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response1 = EngineDetailResponse([process1, process2], [request1, request2])
    response_process_diff = EngineDetailResponse([process1], [request1, request2])
    response_request_diff = EngineDetailResponse([process1, process2], [request2, request1])

    # When
    are_equal1 = response1.__eq__(response_process_diff)
    are_equal2 = response1.__eq__(response_request_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)


  def test_equal_true(self) -> None:
    # Given
    request1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    request2 = RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, datetime(2001, 10, 10, 10, 10, 10, 10))
    process1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    process2 = ProcessDetailResponse(2, ProcessState.IDLE, 2, datetime(2001, 10, 10, 10, 10, 10, 10))
    response1 = EngineDetailResponse([process1, process2], [request1, request2])
    response2 = EngineDetailResponse([process1, process2], [request1, request2])

    # When
    are_equal = response1.__eq__(response2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
