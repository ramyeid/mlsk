#!/usr/bin/python3

from __future__ import annotations
import pandas as pd


class ClassifierResponse:
  '''
  Represents the response that will be returned by controller
  Specifies the result of the computation

  Attributes
    request_id (int)   - unique id assigned to a request
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
    return isinstance(other, ClassifierResponse) and\
      self.request_id == other.request_id and\
      self.column_name == other.column_name and\
      self.values == other.values


  def to_json(self) -> dict:
    return dict(requestId=self.request_id, columnName=self.column_name, values=self.values)


  @classmethod
  def from_data_frame(cls, data_frame: pd.DataFrame, request_id: int, column_name: str) -> ClassifierResponse:
    values_as_int = [int(value) for value in data_frame[column_name].values]

    return cls(request_id, column_name, values_as_int)