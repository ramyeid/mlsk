#!/usr/bin/python3

from typing import Any, Callable, Dict, List, Tuple
from time import sleep
from threading import Thread
from multiprocessing import Pipe, Value, Lock
from multiprocessing.connection import Connection
from utils.logger import LoggerInfo, setup_logger
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process import Process, ProcessState, ProcessStateHolder
from process_pool.monitoring_process import MonitoringProcess
from process_pool.task_result import TaskResult
from process_pool.task import Task


class ProcessPoolException(Exception):
  '''
  Exception thrown by the process pool
  '''
  pass


# TODO implement an inflight task list in order to release the task if the process is manually restarted
class ProcessPool:
  '''
  Represents a pool of processes initialized.

  The number of processes in the pool is tightly coupled with the number of requests an engine can conccurrently handle.

  Note: Initially we were spinning processes on the fly, but the latency of the engine was highly affected.
  Spinning up a Process is not cheap especially considering that all objects in memory will be pickled.

  Attributes
    multiprocessing_manager (MultiProcessingManager)              - Custom SyncManager to create objects shared across multiple processes.
    logger_info             (LoggerInfo)                          - Wrapper for the information needed to create a new logger in the subproceses.
    task_queue              (Queue)                               - Queue containing tasks to be executed asynchronously on processes.
    processes               (Dict[int, Process])                  - Dict of processes to execute and maintain state per process id.
    monitor_process         (MonitoringProcess)                   - Process used to monitor the ongoing processes and to dump a log if any process is stuck.
  '''

  def __init__(self, multiprocessing_manager: MultiProcessingManager, process_count: int, task_queue_size: int, logger_info: LoggerInfo, monitor_interval: int=20, monitor_stuck_threshold: int=40):
    self.multiprocessing_manager = multiprocessing_manager
    self.logger_info = logger_info
    self.task_queue =  multiprocessing_manager.Queue(task_queue_size)
    self.processes = {i: Process(multiprocessing_manager.ProcessStateHolder(), self.task_queue) for i in range(process_count)}
    processes_state_holders = {id: process.get_state_holder() for id, process in self.processes.items()}
    self.monitor_process  = MonitoringProcess(multiprocessing_manager, processes_state_holders, monitor_interval, monitor_stuck_threshold, self.logger_info)


  def start(self) -> None:
    '''
    Start all processes
    '''
    self.monitor_process.start()
    for process in self.processes.values():
      process.start()


  def shutdown(self) -> None:
    '''
    Terminate all processes
    '''
    self.monitor_process.terminate()
    for process in self.processes.values():
      process.terminate()


  def turn_on_monitoring(self) -> None:
    '''
    Start / Restart monitoring the processes
    '''
    self.monitor_process.turn_on()


  def turn_off_monitoring(self) -> None:
    '''
    Stop monitoring the processes
    '''
    self.monitor_process.turn_off()


  def restart_process(self, id: int) -> None:
    '''
    Restart a process abruptly.

    This is used in case a certain process is stuck.
    '''
    self.processes[id].terminate()
    self.processes[id] = Process(self.multiprocessing_manager.ProcessStateHolder(), self.task_queue)
    self.processes[id].start()
    sleep(1) # Jitter to wait until process is up and running


  def is_task_queue_empty(self) -> None:
    '''
    Returns true if task_queue is empty
    '''
    return self.task_queue.empty()


  def get_multiprocessing_manager(self) -> MultiProcessingManager:
    return self.multiprocessing_manager


  def get_process_state(self) -> Dict[int, ProcessState]:
    '''
    Returns the states of all processes
    '''
    return {id: process.get_state() for id, process in self.processes.items()}


  def get_process_state_holders(self) -> Dict[int, ProcessStateHolder]:
    '''
    Returns the states holders of all processes
    '''
    return {id: process.get_state_holder() for id, process in self.processes.items()}


  def execute(self, func: Callable[..., Any], func_args: List[Any]) -> Connection:
    '''
    Execute callable on one of the processes in the pool

    Arguments
      func      (Callable)  - body of the method to execute asynchronously
      func_args (List)      - Args to be injected in `func`

    Returns
      Pipe's connection.
      We guarantee that the item posted in this Pipe will be of type TaskResult.
      TaskResult#get will raise the exception thrown or return the result computed.
    '''

    self.__throw_if_no_idle_process()

    task = Task(func ,func_args)
    self.task_queue.put_nowait(task)
    return task.result_rx


  def any_of(self,
             func1_and_args: Tuple[Callable[..., Any], List[Any]],
             func2_and_args: Tuple[Callable[..., Any], List[Any]],
             unblock_func1: Tuple[Callable[..., Any], List[Any]] = None,
             unblock_func2: Tuple[Callable[..., Any], List[Any]] = None) -> Any:
    '''
    Equivalent of Java's CompletableFuture#anyOf.
    Return the fastest of two callables.

    In order to achiveve this logic in python we will:

    1. Create a pipe for the quickest_result (whether successful or exception)
    2. Call self.execute and hence queue the task containing self#_any_of_process_func
    3. Wait for the reception of a TaskResult in the quickest_result rx, and call TaskResult#get.
    4. Finally, some callables are never ending and the caller should provide a way to kill these callables, in order for the process not to be stuck forever.

    Note:
      - It is important to note that the any_of will be launched in 1 process but we will have two threads running on this process handling both callables.
    '''
    quickest_result_rx, quickest_result_tx = Pipe()
    was_result_posted = self.multiprocessing_manager.Value('resultAlreadyPosted', False)
    result_posting_lock = self.multiprocessing_manager.Lock()

    self.execute(self._any_of_process_func, [quickest_result_tx, was_result_posted, result_posting_lock, func1_and_args, func2_and_args])

    try:
      task_result = quickest_result_rx.recv()
      return task_result.get()
    finally:
      if unblock_func1 is not None:
        unblock_func1[0](*unblock_func1[1])
      if unblock_func2 is not None:
        unblock_func2[0](*unblock_func2[1])


  @classmethod
  def _any_of_process_func(cls,
                quickest_result_tx: Connection,
                was_result_posted: Value,
                result_posting_lock: Lock,
                func1_and_args: Tuple[Callable[..., Any], List[Any]],
                func2_and_args: Tuple[Callable[..., Any], List[Any]]) -> None:
    '''
    Spin up two threads and launch self#_any_of_single_thread_func with two different callables.

    This method represents the async function injected in the TaskQueue to be launched.
    '''
    thread1 = Thread(target=cls._any_of_single_thread_func, args=([func1_and_args[0], func1_and_args[1], quickest_result_tx, was_result_posted, result_posting_lock]))
    thread2 = Thread(target=cls._any_of_single_thread_func, args=([func2_and_args[0], func2_and_args[1], quickest_result_tx, was_result_posted, result_posting_lock]))

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()


  @classmethod
  def _any_of_single_thread_func(cls, func: Callable[..., Any], func_args: List[Any], quickest_result_tx: Connection, was_result_posted: Value, result_posting_lock: Lock) -> None:
    '''
    Call `func` callable.
    Post the TaskResult in `quickest_result_tx` Pipe.
    '''
    result = None
    raised_exception = None
    did_fail = None
    try:
      result = func(*func_args)
      did_fail = False
    except Exception as raised_exception_in:
      raised_exception = raised_exception_in
      did_fail = True
    finally:
      # The first callable to complete would post the result, after which the `quickest_result_tx` would be destroyed.
      # In order to avoid a BrokenPipError, we will check if a result was posted before posting another.
      # This becomes an obvious critical section that needs to be synchronized by a mutex, to make sure that the fastest callable
      # will be able to set `was_result_posted` to true before another accesses it.
      result_posting_lock.acquire()
      if not was_result_posted.get():
        task_result = TaskResult(did_fail, result=result, raised_exception=raised_exception)
        quickest_result_tx.send(task_result)
        was_result_posted.set(True)
      result_posting_lock.release()


  def __throw_if_no_idle_process(self) -> None:
    if ProcessState.IDLE not in self.get_process_state().values():
      raise ProcessPoolException('No Process is IDLE, something is wrong with the java layer orchestrator, we are getting more requests than possible!')
