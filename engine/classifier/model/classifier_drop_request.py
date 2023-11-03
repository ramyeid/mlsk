#!/usr/bin/python3

from __future__ import annotations


class ClassifierDropRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to drop a specific request

  Attributes
    request_id (int)  - unique id assigned to a request
  '''

  def __init__(self, request_id: int):
    self.request_id = request_id


  def get_request_id(self) -> int:
    return self.request_id


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierDropRequest) and\
      self.request_id == other.request_id


  def to_json(self) -> dict:
    return dict(requestId=self.request_id)


  @classmethod
  def from_json(cls, data: dict) -> ClassifierDropRequest:
    request_id = int(data['requestId'])

    return cls(request_id)
