#!/usr/bin/python3

from engine_state import Engine
from process_pool.process_pool import ProcessPool
from admin.model.engine_detail_response import EngineDetailResponse
from admin.model.process_detail_response import ProcessDetailResponse
from admin.model.request_detail_response import RequestDetailResponse
from admin.model.release_request import ReleaseRequest
from admin.model.restart_process_request import RestartProcessRequest
from admin.model.stop_process_request import StopProcessRequest


class AdminService:
  '''
  Service that will allow customer to access and execute admin action and retrieve global details of the engine

  Attributes
    engine (Engine)             - Engine state
    process_pool (ProcessPool)  - Pool of processes running on engine
  '''


  def __init__(self, engine: Engine, process_pool: ProcessPool):
    self.engine = engine
    self.process_pool = process_pool


  def ping(self) -> EngineDetailResponse:
    '''
    Get engine information: processes running, their state and the current inflight requests
    All information are retrieved from engine and process pool

    Returns
      EngineDetailResponse -> Model containing engine state information and processes running
    '''

    inflight_request_snapshot = list(self.engine.get_inflight_requests().values())
    process_state_holders_snapshot = dict(self.process_pool.get_process_state_holders())

    inflight_requests = []
    for request in inflight_request_snapshot:
      inflight_requests.append(
        RequestDetailResponse(
          request.get_request_id(),
          request.get_request_type(),
          request.get_creation_datetime()
        )
      )

    ongoing_processes = []
    for id, process_state_holder in process_state_holders_snapshot.items():
      ongoing_processes.append(
        ProcessDetailResponse(
          id,
          process_state_holder.get(),
          process_state_holder.get_flip_flop_count(),
          process_state_holder.get_start_datetime()
        )
      )

    return EngineDetailResponse(ongoing_processes, inflight_requests)


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
