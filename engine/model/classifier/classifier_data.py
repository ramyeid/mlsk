#!/usr/bin/python3

from __future__ import annotations
import pandas as pd


class ClassifierDataBuilder:
  '''
  Represents the builder used in the controller start, on_data and computation
  Used to group the state and variables for computation

  Attributes
    prediction_column_name ([str]) - name of the column that contains the data to predict
    action_column_names (str)      - name of the columns that are used to predict
    number_of_values (int)         - compute how many values in the future.
    data (dict)                    - values of columns
  '''

  def __init__(self):
    self.prediction_column_name = None
    self.action_column_names = None
    self.number_of_values = None
    self.data = {}


  def get_prediction_column_name(self) -> str:
    return self.prediction_column_name


  def get_action_column_names(self) -> [str]:
    return self.action_column_names


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def get_data(self) -> dict:
    return self.data


  def set_start_data(self, prediction_column_names: str, action_column_names: [str], number_of_values: int) -> None:
    self.prediction_column_name = prediction_column_names
    self.action_column_names = action_column_names
    self.number_of_values = number_of_values


  def contains_start_data(self) -> bool:
    return (self.prediction_column_name is not None) and \
           (self.action_column_names is not None) and \
           (self.number_of_values is not None)


  def add_data(self, column_name: str, values: [int]) -> None:
    self.data[column_name] = values


  def contains_data(self) -> bool:
    return bool(self.data)


  def reset(self) -> None:
    self.__init__()


  def build_classifier_data(self) -> ClassifierData:
    return ClassifierData(self.prediction_column_name, self.action_column_names, self.number_of_values, self.data)



class ClassifierData:
  '''
  Represents the data used in the controller to build the service
  Used to group the state and variables for computation

  Attributes
    prediction_column_name ([str]) - name of the column that contains the data to predict
    action_column_names (str)      - name of the columns that are used to predict
    number_of_values (int)         - compute how many values in the future.
    data (dict)                    - values of columns
  '''

  def __init__(self, prediction_column_name: str, action_column_names: [str], number_of_values: int, data: dict):
    self.prediction_column_name = prediction_column_name
    self.action_column_names = action_column_names
    self.number_of_values = number_of_values
    self.data = data


  def get_prediction_column_name(self) -> str:
    return self.prediction_column_name


  def get_action_column_names(self) -> [str]:
    return self.action_column_names


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def get_data(self) -> dict:
    return self.data


  def to_data_frame(self) -> pd.DataFrame:
    to_series = lambda values: pd.Series(values, index=list(map(lambda i: str(i), range(len(values)))))
    new_data = dict(map(lambda key_value: (key_value[0], to_series(key_value[1])), self.data.items()))
    return pd.DataFrame(new_data)


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierData) and\
      self.prediction_column_name == other.prediction_column_name and\
      self.action_column_names == other.action_column_names and\
      self.number_of_values == other.number_of_values and\
      self.data == other.data


  def to_json(self) -> dict:
    return dict(predictionColumnName=self.prediction_column_name, actionColumnNames=self.action_column_names,
                numberOfValues=self.number_of_values, data=self.data)


