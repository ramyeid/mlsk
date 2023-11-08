#!/usr/bin/python3

from __future__ import annotations
from classifier.model.classifier_type import ClassifierType


class ClassifierStartRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to specify that a request is launched

  Attributes
    request_id (int)                 - unique id assigned to a request
    prediction_column_name (str)     - name of the column used for prediction
    action_column_names ([str])      - names of the columns used for computation
    number_of_values (int)           - count of values to apply computation
    classifier_type (ClassifierType) - classifier algorithm to use
  '''

  def __init__(self, request_id: int, prediction_column_name: str, action_column_names: [str], number_of_values: int, classifier_type: ClassifierType):
    self.request_id = request_id
    self.prediction_column_name = prediction_column_name
    self.action_column_names = action_column_names
    self.number_of_values = number_of_values
    self.classifier_type = classifier_type


  def get_request_id(self) -> int:
    return self.request_id


  def get_prediction_column_name(self) -> str:
    return self.prediction_column_name


  def get_action_column_names(self) -> [str]:
    return self.action_column_names


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def get_classifier_type(self) -> ClassifierType:
    return self.classifier_type


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierStartRequest) and\
      self.request_id == other.request_id and\
      self.prediction_column_name == other.prediction_column_name and\
      self.action_column_names == other.action_column_names and\
      self.number_of_values == other.number_of_values and\
      self.classifier_type == other.classifier_type


  def to_json(self) -> dict:
    return dict(requestId=self.request_id, predictionColumnName=self.prediction_column_name, actionColumnNames=self.action_column_names, numberOfValues=self.number_of_values, classifierType=str(self.classifier_type))


  @classmethod
  def from_json(cls, data: dict) -> ClassifierStartRequest:
    request_id = int(data['requestId'])
    prediction_column_name = str(data['predictionColumnName'])
    action_column_names = list(map(lambda json_in: str(json_in), data['actionColumnNames']))
    number_of_values = int(data['numberOfValues'])
    classifier_type = ClassifierType[data['classifierType']]

    return cls(request_id, prediction_column_name, action_column_names, number_of_values, classifier_type)
