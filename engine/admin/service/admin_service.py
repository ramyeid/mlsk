#!/usr/bin/python3

from admin.model.engine import Engine
from admin.model.release_request import ReleaseRequest
from admin.model.restart_process_request import RestartProcessRequest
from admin.model.stop_process_request import StopProcessRequest


class AdminService:
  '''
  Service that will allow customer to access and execute admin action

  Attributes
    number_of_values (int)  - predict how many values in the future.
  '''


  def __init__(self):
    pass


  def ping(self) -> Engine:
    '''
    Get engine information, from processes running, their state and the current inflight requests

    Returns
      Engine -> Model containing engine state information
    '''
    pass


  def release_request(self, release_request: ReleaseRequest) -> None:
    '''
    Release an inflight request
    '''
    pass


  def stop_process(self, stop_process_request: StopProcessRequest) -> None:
    '''
    Stop a process
    '''
    pass


  def start_process(self) -> None:
    '''
    Start a new process
    '''
    pass


  def restart_process(self, restart_process_request: RestartProcessRequest) -> None:
    '''
    Restart a process
    '''
    pass


  def restart_engine(self) -> None:
    '''
    Restart an engine
    '''
    pass
