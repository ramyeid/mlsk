#!/usr/bin/python3

from typing import List
from time import sleep
from datetime import datetime
import multiprocessing
from multiprocessing import Value
from utils.logger import LoggerInfo, setup_logger
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process import ProcessState, ProcessStateHolder


class MonitoringProcess:
  '''
  Wrapper over multiprocessing.Process in charge of monitoring other processes' state
  '''

  def __init__(self, multiprocessing_manager: MultiProcessingManager, process_state_holders: List[ProcessStateHolder], monitor_interval: int, stuck_threshold: int, logger_info: LoggerInfo):
    self.should_monitor = multiprocessing_manager.Value('should_monitor', True)
    self.multiprocessing_process  = multiprocessing.Process(target=self._monitor_processes_async, args=([self.should_monitor, process_state_holders, monitor_interval, stuck_threshold, logger_info]))


  def start(self) -> None:
    '''
    Start the multiprocessing process
    '''
    self.multiprocessing_process.start()


  def turn_on(self) -> None:
    '''
    Start / Restart monitoring the processes, by simply setting the shared flag to True
    '''
    self.should_monitor.set(True)


  def turn_off(self) -> None:
    '''
    Stop monitoring the processes, by simply setting the shared flag to False
    '''
    self.should_monitor.set(False)


  def terminate(self) -> None:
    '''
    Terminate the multiprocessing process
    '''
    self.should_monitor.set(False)
    self.multiprocessing_process.terminate()
    self.multiprocessing_process.join()
    self.multiprocessing_process.close()


  @classmethod
  def _monitor_processes_async(cls, should_monitor: Value, process_state_holders: List[ProcessStateHolder], monitor_interval: int, stuck_threshold: int, logger_info: LoggerInfo) -> None:
    '''
    1. Caching initial state and flip flop count and last flip flop date
    2. For every `monitor_interval` loop over all state holders
      - If the flip flop count is incremented, update the cached state, flip flop count and date
      - If the flip flop count did not change, check if
        - The interval between the last flip flop and now is greater than `stuck_threshold`
        - The current state of the process is equal to the last cached state
        - The current state is BUSY
        - If all above are true, dump a log signaling that the process handled might be stuck
    '''
    logger = setup_logger(logger_info)
    latest_states = [process_state_holder.get() for process_state_holder in process_state_holders]
    latest_flip_flop_counts = [process_state_holder.get_flip_flop_count() for process_state_holder in process_state_holders]
    latest_flip_flop_date_times = [datetime.now() for process_state_holder in process_state_holders]
    while should_monitor.get():
      try:
        logger.debug('[MonitorProcessPool][Start] Monitoring current %s processes at %s' % (len(process_state_holders), str(datetime.now())))
        current_states = [process_state_holder.get() for process_state_holder in process_state_holders]
        current_flip_flop_counts = [process_state_holder.get_flip_flop_count() for process_state_holder in process_state_holders]

        logger.debug('[MonitorProcessPool] Current States: %s' % (current_states))
        logger.debug('[MonitorProcessPool] Current FlipFlop count: %s' % (current_flip_flop_counts))
        logger.debug('[MonitorProcessPool] Latest FlipFlop States: %s' % (latest_states))
        logger.debug('[MonitorProcessPool] Latest FlipFlop count: %s' % (latest_flip_flop_counts))
        logger.debug('[MonitorProcessPool] Latest FlipFlop Time: %s' % ([str(flip_flop_date) for flip_flop_date in latest_flip_flop_date_times]))

        for i in range(len(process_state_holders)):
          if (current_flip_flop_counts[i] != latest_flip_flop_counts[i]):
            # Let's update the current state in this process!
            latest_states[i] = process_state_holders[i].get()
            latest_flip_flop_counts[i] = process_state_holders[i].get_flip_flop_count()
            latest_flip_flop_date_times[i] = datetime.now()

          else:
            # We did change status in the last 3 seconds, let's check if we're stuck at BUSY for longer than 5 seconds!
            elapsed_since_last_flip_flop_seconds = (datetime.now() - latest_flip_flop_date_times[i]).total_seconds()
            if elapsed_since_last_flip_flop_seconds > stuck_threshold and\
                current_states[i] == latest_states[i] and\
                current_states[i] == ProcessState.BUSY:
              logger.warning('[MonitorProcessPool] The task running on process `%s` has been running for longer than %s seconds, something is wrong - consider restarting the process!' % (i, elapsed_since_last_flip_flop_seconds))
      except Exception as exception:
        logger.error('[MonitorProcessPool] Exception while monitoring: %s' % (exception))
      finally:
        logger.debug('[MonitorProcessPool][End] Monitoring current %s processes at %s' % (len(process_state_holders), datetime.now()))
        sleep(monitor_interval)
