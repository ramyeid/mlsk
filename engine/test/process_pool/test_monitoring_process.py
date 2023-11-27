#!/usr/bin/python3

import unittest
import re
import logging
from logging import getLogger
from time import sleep
from queue import Queue
from threading import Thread
from utils.logger import LoggerInfo
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.monitoring_process import MonitoringProcess
from process_pool.process import ProcessStateHolder, ProcessState
from multiprocessing.util import LOGGER_NAME


def turn_off_monitoring(monitoring_process: MonitoringProcess, sleep_time: int) -> None:
  sleep(sleep_time)
  monitoring_process.turn_off()


class TestMonitoringProcess(unittest.TestCase):


  @classmethod
  def setUpClass(cls) -> None:
    MultiProcessingManager.register('LoggerInfo', LoggerInfo)
    cls.multiprocessing_manager = MultiProcessingManager()
    cls.multiprocessing_manager.start()
    cls.logger_info = cls.multiprocessing_manager.LoggerInfo(None, None, logging.DEBUG)


  def test_should_correctly_start_and_stop_process(self) -> None:
    # Given
    monitor_process = MonitoringProcess(self.multiprocessing_manager, [], 4, 1, self.logger_info)

    # When
    monitor_process.start()
    is_process_alive_pre_terminate = monitor_process.multiprocessing_process.is_alive()
    monitor_process.terminate()

    # Then
    self.assertTrue(is_process_alive_pre_terminate)


  def test_monitoring_should_signal_the_processes_that_are_stuck(self) -> None:
    # Given
    _logger = getLogger('Engine')
    blocked_process_id = 0
    process_state_holders = {}
    for i in range(0, 3):
      process_state_holders[i] = ProcessStateHolder()
    process_state_holders[blocked_process_id].to_busy()
    monitor_process = MonitoringProcess(self.multiprocessing_manager, process_state_holders, 4, 1, LoggerInfo(None, None))

    # When
    thread = Thread(target=turn_off_monitoring, args=[monitor_process, 6])
    thread.start()
    with self.assertLogs(logger='Engine', level=logging.DEBUG) as logContext:
      # Launch monitor process action
      MonitoringProcess._monitor_processes_async(monitor_process.should_monitor, process_state_holders, 4, 1, self.logger_info)
      # Make sure that the thread created could end
      thread.join()

    # Then
    self.assert_on_monitoring_logs(logContext, blocked_process_id)


  def assert_on_monitoring_logs(self, logContext, blocked_process_id: int) -> None:
    '''
    Logs emitted will look something like

    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-27 10:52:37.466812
    [MonitorProcessPool] Current States: {0: 'IDLE', 1: 'BUSY', 2: 'IDLE'}
    [MonitorProcessPool] Current FlipFlop count: {0: 1, 1: 2, 2: 1}
    [MonitorProcessPool] Latest FlipFlop States: {0: 'IDLE', 1: 'BUSY', 2: 'IDLE'}
    [MonitorProcessPool] Latest FlipFlop count: {0: 1, 1: 2, 2: 1}
    [MonitorProcessPool] Latest FlipFlop Time: {0: '2023-11-27 10:52:36.508150', 1: '2023-11-27 10:52:37.465471', 2: '2023-11-27 10:52:36.506841'}
    [MonitorProcessPool][End] Monitoring current 3 processes at 2023-11-27 10:52:37.467719
    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-27 10:52:41.473246
    [MonitorProcessPool] Current States: {0: 'IDLE', 1: 'BUSY', 2: 'IDLE'}
    [MonitorProcessPool] Current FlipFlop count: {0: 1, 1: 2, 2: 1}
    [MonitorProcessPool] Latest FlipFlop States: {0: 'IDLE', 1: 'BUSY', 2: 'IDLE'}
    [MonitorProcessPool] Latest FlipFlop count: {0: 1, 1: 2, 2: 1}
    [MonitorProcessPool] Latest FlipFlop Time: {0: '2023-11-27 10:52:36.508150', 1: '2023-11-27 10:52:37.465471', 2: '2023-11-27 10:52:36.506841'}
    [MonitorProcessPool] The task running on process `1` has been running for longer than 4.009549 seconds, something is wrong - consider restarting the process!
    [MonitorProcessPool][End] Monitoring current 3 processes at 2023-11-27 10:52:41.475100
    '''

    all_logs_recorded = [record.message for record in logContext.records]
    logs_count = len(all_logs_recorded)

    self.assertEqual(15, logs_count)

    log_since_monitor_start = 0
    for i in range(0, logs_count):
      log_since_monitor_start = log_since_monitor_start + 1
      current_log = all_logs_recorded[i]

      if '[Start]' in current_log:
        log_since_monitor_start = 0

      self.assertRegex(current_log, self.get_pattern_for_log(logs_count, i, log_since_monitor_start, blocked_process_id))


  def get_pattern_for_log(self, logs_count: int, current_log_index: int, log_since_monitor_start: int, blocked_process_id: int) -> str:
    if log_since_monitor_start == 0:
      return r'\[MonitorProcessPool\]\[Start\] Monitoring current 3 processes at \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'
    elif log_since_monitor_start == 1:
      if blocked_process_id == 0:
        return r"\[MonitorProcessPool\] Current States: \{0: 'BUSY', 1: 'IDLE', 2: 'IDLE'\}"
      elif blocked_process_id == 1:
        return r"\[MonitorProcessPool\] Current States: \{0: 'IDLE', 1: 'BUSY', 2: 'IDLE'\}"
      else:
        return r"\[MonitorProcessPool\] Current States: \{0: 'IDLE', 1: 'IDLE', 2: 'BUSY'\}"
    elif log_since_monitor_start == 2:
      if blocked_process_id == 0:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \{0: 1, 1: 0, 2: 0\}'
      elif blocked_process_id == 1:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \{0: 0, 1: 1, 2: 0\}'
      else:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \{0: 0, 1: 0, 2: 1\}'
    elif log_since_monitor_start == 3:
      if blocked_process_id == 0:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \{0: 'BUSY', 1: 'IDLE', 2: 'IDLE'\}"
      elif blocked_process_id == 1:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \{0: 'IDLE', 1: 'BUSY', 2: 'IDLE'\}"
      else:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \{0: 'IDLE', 1: 'IDLE', 2: 'BUSY'\}"
    elif log_since_monitor_start == 4:
      if blocked_process_id == 0:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \{0: 1, 1: 0, 2: 0\}'
      elif blocked_process_id == 1:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \{0: 0, 1: 1, 2: 0\}'
      else:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \{0: 0, 1: 0, 2: 1\}'
    elif log_since_monitor_start == 5:
        return r"\[MonitorProcessPool\] Latest FlipFlop Time: \{0: '\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*', 1: '\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*', 2: '\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'\}"
    elif log_since_monitor_start == 6:
      if (current_log_index + 2 == logs_count):
        if blocked_process_id == 0:
          return r'\[MonitorProcessPool\] The task running on process `0` has been running for longer than \d\.\d* seconds, something is wrong - consider restarting the process!'
        elif blocked_process_id == 1:
          return r'\[MonitorProcessPool\] The task running on process `1` has been running for longer than \d\.\d* seconds, something is wrong - consider restarting the process!'
        else:
          return r'\[MonitorProcessPool\] The task running on process `2` has been running for longer than \d\.\d* seconds, something is wrong - consider restarting the process!'
      else:
        return r'\[MonitorProcessPool\]\[End\] Monitoring current 3 processes at \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'
    elif log_since_monitor_start == 7:
        return r'\[MonitorProcessPool\]\[End\] Monitoring current 3 processes at \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'
    else:
        raise Exception('should never arrive here!')


if __name__ == '__main__':
  unittest.main()
