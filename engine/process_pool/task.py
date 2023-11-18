#!/usr/bin/python3

from typing import Any, Callable, List
from multiprocessing import Pipe


class Task:
  '''
  Task to be enqueued in the ProcessPool#task_queue to be launched asynchronously

  This class will also contain the pipe in which the result of the callable will be posted to.

  Note: This class is only for internal use and will never be used by an external caller of the ProcessPool.
  '''

  def __init__(self, func: Callable[..., Any], func_args: List[Any]):
    self.result_rx, self.result_tx = Pipe()
    self.func = func
    self.func_args = func_args
