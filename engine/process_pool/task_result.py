#!/usr/bin/python3

from typing import Any
from multiprocessing import Pipe


class TaskResult:
  '''
  Result of a task launched in the ProcessPool
  A result could be an exception that was raised or the result of the callable!
  '''

  def __init__(self, failed: bool, result: Any=None, raised_exception: Exception=None):
    self.failed = failed
    self.result = result
    self.raised_exception = raised_exception


  def get(self) -> Any:
    if self.failed:
      raise self.raised_exception
    else:
      return self.result
