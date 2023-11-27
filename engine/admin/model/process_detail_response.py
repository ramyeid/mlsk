#!/usr/bin/python3

from datetime import datetime
from process_pool.process import ProcessState


class ProcessDetailResponse:
  '''
  Represents the response returned by admin's ping.
  Contains all information and details about the ongoing state of a process

  Attributes
    id (int)                  - Unique identifier of a process
    state (ProcessState)      - State of the process
    flip_flop_count (int)     - Count of times the status was bounced
    start_datetime (datetime) - datetime of when the process was started
  '''


  def __init__(self, id: int, state: ProcessState, flip_flop_count: int, start_datetime: datetime):
    self.id = id
    self.state = state
    self.flip_flop_count = flip_flop_count
    self.start_datetime = start_datetime


  def get_id(self) -> int:
    return self.id


  def get_state(self) -> ProcessState:
    return self.state


  def get_flip_flop_count(self) -> int:
    return self.flip_flop_count


  def get_start_datetime(self) -> datetime:
    return self.start_datetime


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ProcessDetailResponse) and\
      self.id == other.id and\
      self.state == other.state and\
      self.flip_flop_count == other.flip_flop_count and\
      self.start_datetime == other.start_datetime


  def to_json(self) -> dict:
    return dict(
      id=self.id,
      state=self.state.name,
      flipFlopCount=self.flip_flop_count,
      startDatetime=str(self.start_datetime)
    )
