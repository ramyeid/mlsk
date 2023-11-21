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
    blocked_process_index = 0
    process_state_holders = []
    for i in range(0, 3):
      process_state_holders.append(ProcessStateHolder())
    process_state_holders[blocked_process_index].to_busy()
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
    self.assert_on_monitoring_logs(logContext, blocked_process_index)


  def assert_on_monitoring_logs(self, logContext, blocked_process_index: int) -> None:
    '''
    Logs emitted will look something like

    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-20 15:04:26.666387
    [MonitorProcessPool] Current States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Current FlipFlop count: [1, 0, 0]
    [MonitorProcessPool] Latest FlipFlop States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Latest FlipFlop count: [0, 0, 0]
    [MonitorProcessPool] Latest FlipFlop Time: ['2023-11-20 15:04:26.666153', '2023-11-20 15:04:26.666199', '2023-11-20 15:04:26.666200']
    [MonitorProcessPool][End] Monitoring current 3 processes at 2023-11-20 15:04:30.673102
    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-20 15:04:30.673710
    [MonitorProcessPool] Current States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Current FlipFlop count: [1, 0, 0]
    [MonitorProcessPool] Latest FlipFlop States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Latest FlipFlop count: [1, 0, 0]
    [MonitorProcessPool] Latest FlipFlop Time: ['2023-11-20 15:04:26.668006', '2023-11-20 15:04:26.668407', '2023-11-20 15:04:26.668644']
    [MonitorProcessPool] The task running on process `0` has been running for longer than 4.008075 seconds, something is wrong - consider restarting the process!
    [MonitorProcessPool][End] Monitoring current 3 processes at 2023-11-20 15:04:34.677774
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

      self.assertRegex(current_log, self.get_pattern_for_log(logs_count, i, log_since_monitor_start, blocked_process_index))


  def get_pattern_for_log(self, logs_count: int, current_log_index: int, log_since_monitor_start: int, blocked_process_index: int) -> str:
    if log_since_monitor_start == 0:
      return r'\[MonitorProcessPool\]\[Start\] Monitoring current 3 processes at \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'
    elif log_since_monitor_start == 1:
      if blocked_process_index == 0:
        return r"\[MonitorProcessPool\] Current States: \[<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>\]"
      elif blocked_process_index == 1:
        return r"\[MonitorProcessPool\] Current States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>\]"
      else:
        return r"\[MonitorProcessPool\] Current States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>\]"
    elif log_since_monitor_start == 2:
      if blocked_process_index == 0:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[1, 0, 0\]'
      elif blocked_process_index == 1:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[0, 1, 0\]'
      else:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[0, 0, 1\]'
    elif log_since_monitor_start == 3:
      if blocked_process_index == 0:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>\]"
      elif blocked_process_index == 1:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>\]"
      else:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>\]"
    elif log_since_monitor_start == 4:
      if blocked_process_index == 0:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[1, 0, 0\]'
      elif blocked_process_index == 1:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[0, 1, 0\]'
      else:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[0, 0, 1\]'
    elif log_since_monitor_start == 5:
        return r"\[MonitorProcessPool\] Latest FlipFlop Time: \['\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*', '\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*', '\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d*'\]"
    elif log_since_monitor_start == 6:
      if (current_log_index + 2 == logs_count):
        if blocked_process_index == 0:
          return r'\[MonitorProcessPool\] The task running on process `0` has been running for longer than \d\.\d* seconds, something is wrong - consider restarting the process!'
        elif blocked_process_index == 1:
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
