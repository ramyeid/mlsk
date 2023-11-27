#!/usr/bin/python3

import unittest
import logging
from time import sleep
from datetime import datetime
from queue import Queue
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from utils.logger import LoggerInfo
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process import ProcessStateHolder, ProcessState
from process_pool.process_pool import ProcessPool
from engine_state import Engine, Request, RequestType
from admin.model.process_detail_response import ProcessDetailResponse
from admin.model.request_detail_response import RequestDetailResponse
from admin.model.engine_detail_response import EngineDetailResponse
from admin.service.admin_service import AdminService

class TestAdminService(unittest.TestCase):


  def setUp(self) -> None:
    MultiProcessingManager.register('ProcessStateHolder', ProcessStateHolder)
    MultiProcessingManager.register('Queue', Queue)
    MultiProcessingManager.register('LoggerInfo', LoggerInfo)
    multiprocessing_manager = MultiProcessingManager()
    multiprocessing_manager.start()
    logger_info = multiprocessing_manager.LoggerInfo(None, None, logging.DEBUG)
    process_count = 2
    task_queue_size = 4
    self.process_pool = ProcessPool(multiprocessing_manager, process_count, task_queue_size, logger_info)
    self.engine = Engine()
    self.process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully


  def tearDown(self) -> None:
    self.process_pool.shutdown()


  def test_ping_returns_engine_and_process_pool_info_with_no_inflight_requests(self) -> None:
    # Given
    block_call_rx_1, block_call_tx_1 = Pipe()
    did_reach_func_rx_1, did_reach_func_tx_1 = Pipe()
    block_call_rx_2, block_call_tx_2 = Pipe()
    did_reach_func_rx_2, did_reach_func_tx_2 = Pipe()
    async_result1 = self.process_pool.execute(self.hang, [block_call_rx_1, did_reach_func_tx_1])
    async_result2 = self.process_pool.execute(self.hang, [block_call_rx_2, did_reach_func_tx_2])
    admin_service = AdminService(self.engine, self.process_pool)

    # When
    did_reach_func_rx_1.recv()
    did_reach_func_rx_2.recv()
    actual_engine_detail_response_1 = admin_service.ping()
    block_call_tx_1.send('1')
    async_result1.recv()
    block_call_tx_2.send('1')
    async_result2.recv()
    actual_engine_detail_response_2 = admin_service.ping()

    # Then
    start_datetime_process1 = actual_engine_detail_response_1.get_processes_details()[0].get_start_datetime()
    start_datetime_process2 = actual_engine_detail_response_1.get_processes_details()[1].get_start_datetime()
    expected_engine_detail_response_1 = EngineDetailResponse(
      [
        ProcessDetailResponse(0, ProcessState.BUSY, 2, start_datetime_process1),
        ProcessDetailResponse(1, ProcessState.BUSY, 2, start_datetime_process2),
      ],
      []
    )
    expected_engine_detail_response_2 = EngineDetailResponse(
      [
        ProcessDetailResponse(0, ProcessState.IDLE, 3, start_datetime_process1),
        ProcessDetailResponse(1, ProcessState.IDLE, 3, start_datetime_process2)
      ],
      []
    )
    self.assertEqual(expected_engine_detail_response_1, actual_engine_detail_response_1)
    self.assertEqual(expected_engine_detail_response_2, actual_engine_detail_response_2)


  def test_ping_returns_engine_and_process_pool_info_with_inflight_requests(self) -> None:
    # Given
    block_call_rx_1, block_call_tx_1 = Pipe()
    did_reach_func_rx_1, did_reach_func_tx_1 = Pipe()
    block_call_rx_2, block_call_tx_2 = Pipe()
    did_reach_func_rx_2, did_reach_func_tx_2 = Pipe()
    async_result1 = self.process_pool.execute(self.hang, [block_call_rx_1, did_reach_func_tx_1])
    async_result2 = self.process_pool.execute(self.hang, [block_call_rx_2, did_reach_func_tx_2])
    request1 = Request(1, RequestType.CLASSIFIER)
    request2 = Request(2, RequestType.TIME_SERIES_ANALYSIS)
    request3 = Request(3, RequestType.CLASSIFIER)
    self.engine.register_new_request(request1)
    self.engine.register_new_request(request2)
    self.engine.register_new_request(request3)
    admin_service = AdminService(self.engine, self.process_pool)

    # When
    did_reach_func_rx_1.recv()
    did_reach_func_rx_2.recv()
    actual_engine_detail_response_1 = admin_service.ping()
    block_call_tx_1.send('1')
    async_result1.recv()
    block_call_tx_2.send('1')
    async_result2.recv()
    self.engine.release_request(1)
    self.engine.release_request(3)
    actual_engine_detail_response_2 = admin_service.ping()

    # Then
    start_datetime_process1 = actual_engine_detail_response_1.get_processes_details()[0].get_start_datetime()
    start_datetime_process2 = actual_engine_detail_response_1.get_processes_details()[1].get_start_datetime()
    expected_engine_detail_response_1 = EngineDetailResponse(
      [
        ProcessDetailResponse(0, ProcessState.BUSY, 2, start_datetime_process1),
        ProcessDetailResponse(1, ProcessState.BUSY, 2, start_datetime_process2),
      ],
      [
        RequestDetailResponse(1, RequestType.CLASSIFIER, request1.get_creation_datetime()),
        RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, request2.get_creation_datetime()),
        RequestDetailResponse(3, RequestType.CLASSIFIER, request3.get_creation_datetime())
      ]
    )
    expected_engine_detail_response_2 = EngineDetailResponse(
      [
        ProcessDetailResponse(0, ProcessState.IDLE, 3, start_datetime_process1),
        ProcessDetailResponse(1, ProcessState.IDLE, 3, start_datetime_process2)
      ],
      [
        RequestDetailResponse(2, RequestType.TIME_SERIES_ANALYSIS, request2.get_creation_datetime()),
      ]
    )
    self.assertEqual(expected_engine_detail_response_1, actual_engine_detail_response_1)
    self.assertEqual(expected_engine_detail_response_2, actual_engine_detail_response_2)


  @classmethod
  def hang(cls, block_call_rx: Connection, did_reach_func_tx: Connection) -> None:
    did_reach_func_tx.send('1')
    block_call_rx.recv()


if __name__ == '__main__':
  unittest.main()
