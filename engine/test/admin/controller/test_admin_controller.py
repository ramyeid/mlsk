#!/usr/bin/python3

from typing import Any
import unittest
import json
import pandas as pd
from flask.typing import ResponseReturnValue
from threading import Thread
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from process_pool.process import ProcessState
from engine_server import setup_server
from time_series.service.time_series_analysis_service import TimeSeriesAnalysisService
from time_series.service.time_series_analysis_service_factory import TimeSeriesAnalysisServiceFactory


class MockTimeSeriesAnalysisServiceFactory:


  def __init__(self):
    self.build_mock = False


  def do_build_mock(self, block_call_rx: Connection, did_reach_service_tx: Connection):
    self.build_mock = True
    self.block_call_rx = block_call_rx
    self.did_reach_service_tx = did_reach_service_tx


  def do_build_service(self):
    self.build_mock = False


  def build_service(self, data: pd.DataFrame, date_column_name: str,
                    value_column_name: str, number_of_values: int) -> TimeSeriesAnalysisService:
    if self.build_mock:
      return MockTimeSeriesAnalysisService(self.block_call_rx, self.did_reach_service_tx)
    else:
      return TimeSeriesAnalysisServiceFactory().build_service(data, date_column_name, value_column_name, number_of_values)


class MockTimeSeriesAnalysisService:

  def __init__(self, block_call_rx: Connection, did_reach_service_tx: Connection):
    self.block_call_rx = block_call_rx
    self.did_reach_service_tx = did_reach_service_tx


  def predict(self) -> Any:
    self._notify_service_reached_and_block()
    return None


  def forecast(self) -> Any:
    self._notify_service_reached_and_block()
    return None


  def compute_forecast_accuracy(self) -> Any:
    self._notify_service_reached_and_block()
    return None


  def _notify_service_reached_and_block(self) -> None:
    self.did_reach_service_tx.send('1')
    self.block_call_rx.recv()


class TestAdminController(unittest.TestCase):


  CONTENT_TYPE = 'application/json'


  def setUp(self) -> None:
    self.time_series_analysis_service_factory = MockTimeSeriesAnalysisServiceFactory()
    self.flask_app, self.engine, self.process_pool, self._logger = setup_server(None, None, 'CRITICAL', time_series_analysis_service_factory=self.time_series_analysis_service_factory)
    self.test_app = self.flask_app.test_client()
    self.time_series_analysis_service_factory.do_build_service()


  def tearDown(self) -> None:
    self.process_pool.shutdown()


  def test_ping_with_no_inflight_requests(self) -> None:
    # Given

    # When
    response = self.get_ping()

    # Then
    self.assert_on_response_like(
      r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 0, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 0, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 0, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 0, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 0, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}',
      200,
      response
    )


  def test_ping_with_inflight_requets(self) -> None:
    # Given
    block_call_rx, block_call_tx = Pipe()
    did_reach_service_rx, did_reach_service_tx = Pipe()
    self.time_series_analysis_service_factory.do_build_mock(block_call_rx, did_reach_service_tx)
    request_id = 123
    body = dict(requestId=request_id,
                timeSeries=dict(rows=build_rows(),
                                dateColumnName='Date',
                                valueColumnName='Passengers',
                                dateFormat='yyyy-MM'),
                numberOfValues=2)
    body_as_string = json.dumps(body)

    # When
    # Start thread to post a forecast, which will hang
    thread1 = Thread(target=self.post_forecast, args=[body_as_string])
    thread1.start()
    # Make sure call reached service
    did_reach_service_rx.recv()
    # Now check status of engine
    response = self.get_ping()
    blocked_process_id = self.get_busy_process_id()
    self.process_pool.get_process_state()
    # Unblock calls on TimeSeriesAnalysisService
    block_call_tx.send('1')
    # Make sure both threads are joined and hence completed
    thread1.join()
    # Now check status of engine, agian
    response2 = self.get_ping()

    # Then
    self.assert_on_response_like(
      self.build_expected_regex_response_for_active_request(blocked_process_id),
      200,
      response
    )
    self.assert_on_response_like(
      self.build_expected_regex_response_for_post_request(blocked_process_id),
      200,
      response2
    )


  def get_busy_process_id(self) -> int:
    for id, process_state_holder in self.process_pool.get_process_state_holders().items():
      if process_state_holder.get() == ProcessState.BUSY:
        return id
    return -1


  def build_expected_regex_response_for_active_request(self, blocked_process_id: int) -> str:
    if blocked_process_id == 0:
      return r'\{"processesDetails": \[\{"id": 0, "state": "BUSY", "flipFlopCount": 2, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\{"id": 123, "type": "TIME_SERIES_ANALYSIS", "creationDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\]\}'
    elif blocked_process_id == 1:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "BUSY", "flipFlopCount": 2, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\{"id": 123, "type": "TIME_SERIES_ANALYSIS", "creationDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\]\}'
    elif blocked_process_id == 2:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "BUSY", "flipFlopCount": 2, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\{"id": 123, "type": "TIME_SERIES_ANALYSIS", "creationDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\]\}'
    elif blocked_process_id == 3:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "BUSY", "flipFlopCount": 2, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\{"id": 123, "type": "TIME_SERIES_ANALYSIS", "creationDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\]\}'
    else:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "BUSY", "flipFlopCount": 2, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\{"id": 123, "type": "TIME_SERIES_ANALYSIS", "creationDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\]\}'


  def build_expected_regex_response_for_post_request(self, blocked_process_id: int) -> str:
    if blocked_process_id == 0:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 3, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}'
    elif blocked_process_id == 1:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 3, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}'
    elif blocked_process_id == 2:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 3, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}'
    elif blocked_process_id == 3:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 3, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}'
    else:
      return r'\{"processesDetails": \[\{"id": 0, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 1, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 2, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 3, "state": "IDLE", "flipFlopCount": 1, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}, \{"id": 4, "state": "IDLE", "flipFlopCount": 3, "startDatetime": "\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*"\}\], "inflightRequestsDetails": \[\]\}'


  def assert_on_response_like(self, body_regex: str, status_code: int, response: ResponseReturnValue) -> None:
    self.assertRegex(str(response.data), body_regex)
    self.assertEqual(status_code, response.status_code)


  def get_ping(self) -> ResponseReturnValue:
    return self.test_app.get('/admin/ping', content_type=self.CONTENT_TYPE)


  def post_forecast(self, body_as_string: str) -> ResponseReturnValue:
    return self.test_app.post('/time-series-analysis/forecast', data=body_as_string,
                             content_type=self.CONTENT_TYPE)


def build_rows() -> [dict]:
  return [dict(date='1949-01', value=112.0), dict(date='1949-02', value=118.0),
          dict(date='1949-03', value=132.0), dict(date='1949-04', value=129.0),
          dict(date='1949-05', value=121.0), dict(date='1949-06', value=135.0),
          dict(date='1949-07', value=148.0), dict(date='1949-08', value=148.0),
          dict(date='1949-09', value=136.0), dict(date='1949-10', value=119.0),
          dict(date='1949-11', value=104.0), dict(date='1949-12', value=118.0),
          dict(date='1950-01', value=115.0), dict(date='1950-02', value=126.0),
          dict(date='1950-03', value=141.0), dict(date='1950-04', value=135.0),
          dict(date='1950-05', value=125.0), dict(date='1950-06', value=149.0),
          dict(date='1950-07', value=170.0), dict(date='1950-09', value=158.0),
          dict(date='1950-10', value=158.0), dict(date='1950-11', value=133.0),
          dict(date='1950-12', value=114.0), dict(date='1951-01', value=140.0),
          dict(date='1951-02', value=145.0), dict(date='1951-03', value=150.0),
          dict(date='1951-04', value=178.0), dict(date='1951-05', value=163.0),
          dict(date='1951-05', value=172.0), dict(date='1951-06', value=178.0),
          dict(date='1951-07', value=199.0), dict(date='1951-08', value=199.0),
          dict(date='1951-09', value=184.0), dict(date='1951-10', value=162.0),
          dict(date='1951-11', value=146.0), dict(date='1951-12', value=166.0)]


if __name__ == '__main__':
  unittest.main()
