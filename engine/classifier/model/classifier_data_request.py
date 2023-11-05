#!/usr/bin/python3

from __future__ import annotations


class ClassifierDataRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to specify the data used for computation and prediction

  Attributes
    request_id (int)  - unique id assigned to a request
    column_name (str)  - name of the column of the current data
    values ([int])     - values of the column
  '''

  def __init__(self, request_id: int, column_name: str, values: [int]):
    self.request_id = request_id
    self.column_name = column_name
    self.values = values


  def get_request_id(self) -> int:
    return self.request_id


  def get_column_name(self) -> str:
    return self.column_name


  def get_values(self) -> [int]:
    return self.values


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierDataRequest) and\
      self.request_id == other.request_id and\
      self.column_name == other.column_name and\
      self.values == other.values


  def to_json(self) -> dict:
    return dict(requestId=self.request_id, columnName=self.column_name, values=self.values)


  @classmethod
  def from_json(cls, data: dict) -> ClassifierDataRequest:
    request_id = int(data['requestId'])
    column_name = str(data['columnName'])
    values = list(map(lambda json_in: int(json_in), data['values']))

    return cls(request_id, column_name, values)
