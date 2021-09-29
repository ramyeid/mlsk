#!/usr/bin/python3

from __future__ import annotations


class ClassifierStartRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to specify that a request is launched

  Attributes
    prediction_column_name (str)  - name of the column used for prediction
    action_column_names ([str])   - names of the columns used for computation
    number_of_values (int)        - count of values to apply computation
  '''

  def __init__(self, prediction_column_name: str, action_column_names: [str], number_of_values: int):
    self.prediction_column_name = prediction_column_name
    self.action_column_names = action_column_names
    self.number_of_values = number_of_values


  def get_prediction_column_name(self) -> str:
    return self.prediction_column_name


  def get_action_column_names(self) -> [str]:
    return self.action_column_names


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierStartRequest) and\
      self.prediction_column_name == other.prediction_column_name and\
      self.action_column_names == other.action_column_names and\
      self.number_of_values == other.number_of_values


  def to_json(self) -> dict:
    return dict(predictionColumnName=self.prediction_column_name, actionColumnNames=self.action_column_names, numberOfValues=self.number_of_values)


  @classmethod
  def from_json(cls, data: dict) -> ClassifierStartRequest:
    prediction_column_name = str(data['predictionColumnName'])
    action_column_names = list(map(lambda json_in: str(json_in), data['actionColumnNames']))
    number_of_values = int(data['numberOfValues'])

    return cls(prediction_column_name, action_column_names, number_of_values)
