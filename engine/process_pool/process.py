#!/usr/bin/python3

from enum import Enum
from queue import Queue
import multiprocessing
from process_pool.task_result import TaskResult


class ProcessState(Enum):
  '''
  Represents the state of the process
  '''

  IDLE = 'IDLE'
  BUSY = 'BUSY'


class ProcessStateHolder:
  '''
  Wrapper around ProcessState with a counter that is incremented when the state is bounced.

  Attributes
    state           (ProcessState)  - Enum representing the current state of the process
    flip_flop_count (int)           - Count of times the status was bounced
  '''


  def __init__(self):
    self.state = ProcessState.IDLE
    self.flip_flop_count = 0


  def to_idle(self) -> None:
    '''
    Switch the state to IDLE and increment the flip flop count
    '''
    self.state = ProcessState.IDLE
    self.flip_flop_count = self.flip_flop_count + 1


  def to_busy(self) -> None:
    '''
    Switch the state to BUSY and increment the flip flop count
    '''
    self.state = ProcessState.BUSY
    self.flip_flop_count = self.flip_flop_count + 1


  def get(self) -> ProcessState:
    '''
    Return the ProcessState enum
    '''
    return self.state


  def get_flip_flop_count(self) -> int:
    '''
    Return the flip flop count
    '''
    return self.flip_flop_count


class Process:
  '''
  Wrapper over multiprocessing.Process with a state
  '''

  def __init__(self, state_holder: ProcessStateHolder, task_queue: Queue):
    self.multiprocessing_process  = multiprocessing.Process(target=self._dequeue_and_work, args=([state_holder, task_queue]))
    self.state_holder = state_holder


  def start(self) -> None:
    '''
    Start the multiprocessing process
    '''
    self.multiprocessing_process.start()


  def terminate(self) -> None:
    '''
    Terminate the multiprocessing process
    '''
    self.multiprocessing_process.terminate()
    self.multiprocessing_process.join()
    self.multiprocessing_process.close()


  def get_state(self) -> ProcessState:
    '''
    Returns the state of the process
    '''
    return self.state_holder.get()


  def get_state_holder(self) -> ProcessStateHolder:
    '''
    Returns the thread safe state holder
    '''
    return self.state_holder


  @classmethod
  def _dequeue_and_work(cls, state_holder: ProcessStateHolder, task_queue: Queue):
    '''
    Async function called by all processes.

    Of course we will start with a while True and read from `task_queue` at the beginning
    Upon dequeuing, we will call the `task.func` containing the logic to execute.

    In case of success:
      We will post a successful TaskResult containing the result to `result_tx` Pipe.
    In case of exception:
      We will post a failed TaskResult containing the raised exception in `result_tx` Pipe.
    '''
    state_holder.to_idle()
    while True:
      current_result = None
      current_exception = None
      current_failed = False
      task = task_queue.get()
      try:
        state_holder.to_busy()
        current_result = task.func(*task.func_args)
      except Exception as raised_exception:
        current_failed = True
        current_exception = raised_exception
      finally:
        state_holder.to_idle()
        task.result_tx.send(TaskResult(current_failed, result=current_result, raised_exception=current_exception))
        task_queue.task_done()