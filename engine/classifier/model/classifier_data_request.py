#!/usr/bin/python3

from __future__ import annotations
from classifier.model.classifier_type import ClassifierType


class ClassifierDataRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to specify the data used for computation and prediction

  Attributes
    request_id (int)                 - unique id assigned to a request
    column_name (str)                - name of the column of the current data
    values ([int])                   - values of the column
    classifier_type (ClassifierType) - classifier algorithm to use
  '''

  def __init__(self, request_id: int, column_name: str, values: [int], classifier_type: ClassifierType):
    self.request_id = request_id
    self.column_name = column_name
    self.values = values
    self.classifier_type = classifier_type


  def get_request_id(self) -> int:
    return self.request_id


  def get_column_name(self) -> str:
    return self.column_name


  def get_values(self) -> [int]:
    return self.values


  def get_classifier_type(self) -> ClassifierType:
    return self.classifier_type


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierDataRequest) and\
      self.request_id == other.request_id and\
      self.column_name == other.column_name and\
      self.values == other.values and\
      self.classifier_type == other.classifier_type


  def to_json(self) -> dict:
    return dict(requestId=self.request_id, columnName=self.column_name, values=self.values, classifierType=str(self.classifier_type))


  @classmethod
  def from_json(cls, data: dict) -> ClassifierDataRequest:
    request_id = int(data['requestId'])
    column_name = str(data['columnName'])
    values = list(map(lambda json_in: int(json_in), data['values']))
    classifier_type = ClassifierType[data['classifierType']]

    return cls(request_id, column_name, values, classifier_type)
