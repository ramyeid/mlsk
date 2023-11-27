#!/usr/bin/python3

from typing import List
from datetime import datetime
from engine_state import RequestType


class RequestDetailResponse:
  '''
  Represents the response returned by admin's ping.
  Contains all information and details about the inflight request

  Attributes
    processes_details (List[ProcessDetailResponse])         - All ongoing processes details
    inflight_requests_details (List[RequestDetailResponse]) - All inflights requests details
  '''


  def __init__(self, id: int, type: RequestType, creation_datetime: datetime):
    self.id = id
    self.type = type
    self.creation_datetime = creation_datetime


  def get_id(self) -> int:
    return self.id


  def get_type(self) -> RequestType:
    return self.type


  def get_creation_datetime(self) -> datetime:
    return self.creation_datetime


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, RequestDetailResponse) and\
      self.id == other.id and\
      self.type == other.type and\
      self.creation_datetime == other.creation_datetime


  def to_json(self) -> dict:
    return dict(
      id=self.id,
      type=self.type.name,
      creationDatetime=str(self.creation_datetime)
    )
