#!/usr/bin/python3

from __future__ import annotations
from typing import List
from admin.model.process_detail_response import ProcessDetailResponse
from admin.model.request_detail_response import RequestDetailResponse


class EngineDetailResponse:
  '''
  Represents the response returned by admin's ping.
  Contains all information and details about the ongoing state of the engine and requests

  Attributes
    processes_details (List[ProcessDetailResponse])         - All ongoing processes details
    inflight_requests_details (List[RequestDetailResponse]) - All inflights requests details
  '''


  def __init__(self, processes_details: List[ProcessDetailResponse], inflight_requests_details: List[RequestDetailResponse]):
    self.processes_details = processes_details
    self.inflight_requests_details = inflight_requests_details


  def get_processes_details(self) -> List[ProcessDetailResponse]:
    return self.processes_details


  def get_inflight_requests_details(self) -> List[RequestDetailResponse]:
    return self.inflight_requests_details


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, EngineDetailResponse) and\
      self.processes_details == other.processes_details and\
      self.inflight_requests_details == other.inflight_requests_details


  def to_json(self) -> dict:
    return dict(
      processesDetails=self.processes_details,
      inflightRequestsDetails=self.inflight_requests_details
    )
