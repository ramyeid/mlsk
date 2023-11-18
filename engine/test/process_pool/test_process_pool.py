#!/usr/bin/python3

import unittest
import re
import logging
from time import sleep
from queue import Queue
from threading import Thread
from utils.logger import LoggerInfo
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from process_pool.process_pool import ProcessPool, ProcessPoolException
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process import ProcessStateHolder, ProcessState
from multiprocessing.util import LOGGER_NAME


from logging import getLogger


class MyException(Exception):
  '''
  Custom Exception
  '''


def wait_for_3_seconds() -> str:
  sleep(3)
  return 'waitedFor3Seconds'


def wait_for_to_100000_seconds() -> str:
  sleep(100000)
  return 'waitedFor100000Seconds'


def wait_and_raise_exception(sleep_time: int, exception: Exception) -> str:
  sleep(sleep_time)
  raise exception


def multiply_by_2(x: int) -> str:
  return '%s' % (x * 2)


def wait_until_reception(blocking_task_rx: Connection, was_tasked_launched_tx: Connection) -> None:
  was_tasked_launched_tx.send('1')
  result = blocking_task_rx.recv()
  return result


def release_blocked_task(blocking_task_tx: Connection) -> None:
  blocking_task_tx.send('1')


def turn_off_monitoring(process_pool: ProcessPool, sleep_time: int) -> None:
  sleep(sleep_time)
  process_pool.turn_off_monitoring()


class TestProcessPool(unittest.TestCase):


  @classmethod
  def setUpClass(cls) -> None:
    MultiProcessingManager.register('ProcessStateHolder', ProcessStateHolder)
    MultiProcessingManager.register('Queue', Queue)
    MultiProcessingManager.register('LoggerInfo', LoggerInfo)
    cls.multiprocessing_manager = MultiProcessingManager()
    cls.multiprocessing_manager.start()
    cls.logger_info = cls.multiprocessing_manager.LoggerInfo(None, None, logging.DEBUG)


  def test_should_correctly_restart_a_process_stuck_on_demand(self) -> None:
    # Given
    process_count = 1
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully

    # When
    result_rx_1 = process_pool.execute(wait_for_to_100000_seconds, [])
    sleep(1) # Not synchronizing anything but waiting for the task to start and get stuck
    process_pool.restart_process(0)
    restart_process_status = process_pool.get_process_state()
    result_rx_2 = process_pool.execute(multiply_by_2, [2])
    task_result = result_rx_2.recv()

    # Then
    self.assertEqual(4, int(task_result.get()))
    self.assertEqual([ProcessState.IDLE], restart_process_status)
    self.assertEqual([ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_should_execute_function_asynchronously_and_retrieve_result_via_connection(self) -> None:
    # Given
    process_count = 3
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully

    # When
    result_rx_1 = process_pool.execute(multiply_by_2, [1])
    result_rx_2 = process_pool.execute(multiply_by_2, [2])
    result_rx_3 = process_pool.execute(multiply_by_2, [3])
    task_result_1 = result_rx_1.recv()
    task_result_2 = result_rx_2.recv()
    task_result_3 = result_rx_3.recv()

    # Then
    self.assertEqual(2, int(task_result_1.get()))
    self.assertEqual(4, int(task_result_2.get()))
    self.assertEqual(6, int(task_result_3.get()))
    self.assertEqual([ProcessState.IDLE, ProcessState.IDLE, ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_should_execute_function_asynchronously_and_retrieve_exceptions_that_were_thrown(self) -> None:
    # Given
    process_count = 2
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully

    # When
    result_rx_1 = process_pool.execute(wait_and_raise_exception, [0, MyException('custom exception raised')])
    result_rx_2 = process_pool.execute(wait_and_raise_exception, [0, Exception('parent exception raised')])
    task_result_1 = result_rx_1.recv()
    task_result_2 = result_rx_2.recv()

    # Then
    with self.assertRaises(MyException) as context:
      task_result_1.get()
    self.assertEqual('custom exception raised', str(context.exception))

    with self.assertRaises(Exception) as context:
      task_result_2.get()
    self.assertEqual('parent exception raised', str(context.exception))

    self.assertEqual([ProcessState.IDLE, ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_throw_exception_if_process_pool_receives_task_while_all_processes_are_busy(self) -> None:
    # Given
    process_count = 2
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully
    blocking_task_rx_1, blocking_task_tx_1 = Pipe()
    was_tasked_launched_rx_1, was_tasked_launched_tx_1 = Pipe()
    blocking_task_rx_2, blocking_task_tx_2 = Pipe()
    was_tasked_launched_rx_2, was_tasked_launched_tx_2 = Pipe()

    # When
    result_rx_1 = process_pool.execute(wait_until_reception, [blocking_task_rx_1, was_tasked_launched_tx_1])
    result_rx_2 = process_pool.execute(wait_until_reception, [blocking_task_rx_2, was_tasked_launched_tx_2])
    # Wait until task was dequeued
    was_tasked_launched_rx_1.recv()
    was_tasked_launched_rx_2.recv()
    with self.assertRaises(ProcessPoolException) as context:
      process_pool.execute(multiply_by_2, [3])

    # Then
    self.assertEqual('No Process is IDLE, something is wrong with the java layer orchestrator, we are getting more requests than possible!', str(context.exception))
    self.assertEqual([ProcessState.BUSY, ProcessState.BUSY], process_pool.get_process_state())
    # Unblock tasks and hence processes
    blocking_task_tx_1.send('1')
    blocking_task_tx_2.send('1')
    self.assertEqual(1, int(result_rx_1.recv().get()))
    self.assertEqual(1, int(result_rx_2.recv().get()))
    self.assertEqual([ProcessState.IDLE, ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_return_fastest_of_two_callables(self) -> None:
    # Given
    process_count = 1
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully
    blocking_task_rx, blocking_task_tx = Pipe()
    was_tasked_launched_rx, was_tasked_launched_tx = Pipe()

    # When
    result = process_pool.any_of(
      [wait_for_3_seconds, []],
      [wait_until_reception, [blocking_task_rx, was_tasked_launched_tx]],
      unblock_func2=[release_blocked_task, [blocking_task_tx]]
    )
    sleep(1) # Not synchronizing anything but waiting for func2 to complete

    # Then
    self.assertEqual('waitedFor3Seconds', result)
    self.assertEqual([ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_return_first_thrown_exception(self) -> None:
    # Given
    process_count = 1
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully
    blocking_task_rx, blocking_task_tx = Pipe()
    was_tasked_launched_rx, was_tasked_launched_tx = Pipe()

    # When
    with self.assertRaises(MyException) as context:
      process_pool.any_of(
        [wait_and_raise_exception, [3, MyException('Exception Message')]],
        [wait_until_reception, [blocking_task_rx, was_tasked_launched_tx]],
        unblock_func2=[release_blocked_task, [blocking_task_tx]]
      )
    sleep(1) # Not synchronizing anything but waiting for func2 to complete

    # Then
    self.assertEqual('Exception Message', str(context.exception))
    self.assertEqual([ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def test_process_pool_monitoring_should_signal_the_processes_that_are_stuck(self) -> None:
    # Given
    _logger = getLogger('Engine')
    process_count = 3
    task_queue_size = 4
    process_pool = ProcessPool(self.multiprocessing_manager, process_count, task_queue_size, self.logger_info, monitor_interval=4, monitor_stuck_threshold=1)
    process_pool.start()
    sleep(1) # Introduce jitter to make sure all components have been created successfully
    blocking_task_rx, blocking_task_tx = Pipe()
    was_tasked_launched_rx, was_tasked_launched_tx = Pipe()

    # When
    # Execute an action that will hang
    result_rx = process_pool.execute(wait_until_reception, [blocking_task_rx, was_tasked_launched_tx])
    # Wait until task is received by process
    was_tasked_launched_rx.recv()
    # Create a thread to turn off the monitoring
    thread = Thread(target=turn_off_monitoring, args=[process_pool, 6])
    thread.start()
    with self.assertLogs(logger='Engine', level=logging.DEBUG) as logContext:
      # Launch monitor process action
      process_pool.monitor_process._monitor_processes_async(process_pool.monitor_process.should_monitor, process_pool.get_process_state_holders(), 4, 1, self.logger_info)
      # Make sure that the thread created could end
      thread.join()

    # Then
    blocked_process_index = process_pool.get_process_state().index(ProcessState.BUSY)
    self.assert_on_monitoring_logs(logContext, blocked_process_index)
    blocking_task_tx.send('1')
    self.assertEqual('1', result_rx.recv().get())
    self.assertEqual([ProcessState.IDLE, ProcessState.IDLE, ProcessState.IDLE], process_pool.get_process_state())
    self.assertTrue(process_pool.is_task_queue_empty())
    process_pool.shutdown()


  def assert_on_monitoring_logs(self, logContext, blocked_process_index: int) -> None:
    '''
    Logs emitted will look something like

    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-20 15:04:26.666387
    [MonitorProcessPool] Current States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Current FlipFlop count: [2, 1, 1]
    [MonitorProcessPool] Latest FlipFlop States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Latest FlipFlop count: [0, 0, 0]
    [MonitorProcessPool] Latest FlipFlop Time: ['2023-11-20 15:04:26.666153', '2023-11-20 15:04:26.666199', '2023-11-20 15:04:26.666200']
    [MonitorProcessPool][End] Monitoring current 3 processes at 2023-11-20 15:04:30.673102
    [MonitorProcessPool][Start] Monitoring current 3 processes at 2023-11-20 15:04:30.673710
    [MonitorProcessPool] Current States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Current FlipFlop count: [2, 1, 1]
    [MonitorProcessPool] Latest FlipFlop States: [<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>]
    [MonitorProcessPool] Latest FlipFlop count: [2, 1, 1]
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
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[2, 1, 1\]'
      elif blocked_process_index == 1:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[1, 2, 1\]'
      else:
        return r'\[MonitorProcessPool\] Current FlipFlop count: \[1, 1, 2\]'
    elif log_since_monitor_start == 3:
      if blocked_process_index == 0:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>\]"
      elif blocked_process_index == 1:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>, <ProcessState.IDLE: 'IDLE'>\]"
      else:
        return r"\[MonitorProcessPool\] Latest FlipFlop States: \[<ProcessState.IDLE: 'IDLE'>, <ProcessState.IDLE: 'IDLE'>, <ProcessState.BUSY: 'BUSY'>\]"
    elif log_since_monitor_start == 4:
      if blocked_process_index == 0:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[2, 1, 1\]'
      elif blocked_process_index == 1:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[1, 2, 1\]'
      else:
        return r'\[MonitorProcessPool\] Latest FlipFlop count: \[1, 1, 2\]'
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
