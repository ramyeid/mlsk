#!/usr/bin/python3

from __future__ import annotations


class RestartProcessRequest:
  '''
  Represents the request that will be sent to the admin_service to restart a process

  Attributes
    process_id (int)  - unique id assigned to a process
  '''


  def __init__(self, process_id: int):
    self.process_id = process_id


  def get_process_id(self) -> int:
    return self.process_id


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, RestartProcessRequest) and\
      self.process_id == other.process_id


  def to_json(self) -> dict:
    return dict(processId = self.process_id)


  @classmethod
  def from_json(cls, data: dict) -> RestartProcessRequest:
    process_id = int(data['processId'])
    return cls(process_id)
