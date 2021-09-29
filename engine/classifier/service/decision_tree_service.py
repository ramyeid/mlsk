#!/usr/bin/python3

from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score
import numpy as np
import pandas as pd
from classifier.service.classifier_exception import ClassifierException


class DecisionTreeService:
  '''
  Service that will predict the values using decision tree algorithm

  Attributes
    decision_tree_classifier(DecisionTreeClassifier) - classifier used to predict
    data (pandas.DataFrame) - data frame containing the columns corresponding values
    action_column_names ([str])  - name of the columns that are used to predict
    prediction_column_name (str)  - name of the column that contains the data to predict
    number_of_values (int)  - predict how many values in the future.
  '''

  def __init__(self, data: pd.DataFrame, action_column_names: [str],
                prediction_column_name: str, number_of_values: int):
    self.decision_tree_classifier = DecisionTreeClassifier()
    self.data = data
    self.action_column_names = action_column_names
    self.prediction_column_name = prediction_column_name
    self.number_of_values = number_of_values


  def predict(self) -> pd.DataFrame:
    '''
    Predict the next {number_of_values} values to come using Decision Tree algorithm
    This method will compute Decision tree with feature and target values taken from [data]

    Returns
      pandas.DataFrame -> data frame containing one column with predicted values only
    '''
    new_data = self.__reindex_and_normalize()

    features_train, features_test = self.__split_to_features_train_and_test(new_data)
    target_train = self.__split_to_target_train(new_data)

    target_prediction = self.__predict(features_train, target_train, features_test)

    return pd.DataFrame.from_dict({ self.prediction_column_name: target_prediction })


  def compute_predict_accuracy(self) -> float:
    '''
    Compute the accuracy of the predict service.
    Predict the last {number_of_values} values and compare them to the actual values

    Returns
      float -> computed accuracy of the predict service
    '''
    new_data = self.__reindex_and_normalize()

    features_train, features_test = self.__split_to_features_train_and_test(new_data)
    target_train = self.__split_to_target_train(new_data)
    target_test = self.__split_to_target_test(new_data)

    target_prediction = self.__predict(features_train, target_train, features_test)

    return accuracy_score(target_test, target_prediction, normalize = True) * 100


  def __predict(self, features_train: np.ndarray, target_train: np.ndarray, features_test: np.ndarray) -> np.ndarray:
    '''
    Call decision tree service predict

    Arguments
      features_train (np.ndarray) - array containing all {action_column_names} values used for computation/learning
      target_train (np.ndarray)   - array containing {prediction_column_name} values used for computation/learning
      features_test (np.ndarray)  - array containing all {action_column_names} values used for prediction [lenght = {number_of_values}]

    Returns
      np.ndarray -> predicted values with length = {number_of_values}
    '''
    self.decision_tree_classifier.fit(features_train, target_train)
    return self.decision_tree_classifier.predict(features_test)


  def __split_to_features_train_and_test(self, data_frame: pd.DataFrame) -> [np.ndarray, np.ndarray]:
    '''
    Split data frame into feature train and test ndarrays

    Arguments
      data_frame (np) - reindex and normalized DataFrame

    Returns
      [np.ndarray, np.ndarray] -> features_train, features_test
    '''
    size = len(data_frame[self.action_column_names[0]])

    features = data_frame.values[:,:len(self.action_column_names)]
    features_train = features[:size - self.number_of_values]
    features_test = features[size - self.number_of_values:]

    return [features_train, features_test]


  def __split_to_target_train(self, data_frame: pd.DataFrame) -> np.ndarray:
    '''
    Split data frame into target train ndarray

    Arguments
      data_frame (np) - reindex and normalized DataFrame

    Returns
      np.ndarray -> target_train
    '''
    size = len(data_frame[self.action_column_names[0]])

    target = data_frame.values[:,len(self.action_column_names)]
    target_train = target[:size - self.number_of_values]

    return target_train


  def __split_to_target_test(self, data_frame: pd.DataFrame) -> np.ndarray:
    '''
    Split data frame into target test ndarray containing `actual` values

    Arguments
      data_frame (np) - reindex and normalized DataFrame

    Returns
      np.ndarray -> target_test
    '''
    size = len(data_frame[self.action_column_names[0]])

    target = data_frame.values[:,len(self.action_column_names)]
    target_test = target[size - self.number_of_values:]

    self.__throw_exception_if_target_test_contains_nan(target_test)

    return target_test


  def __reindex_and_normalize(self) -> pd.DataFrame:
    '''
    Reindex data frame by putting the prediction_column_name at the end.
    Normalize the feature values.

    Returns
      pandas.DataFrame -> data frame reindexed and normalized
    '''
    columns = self.action_column_names.copy()
    columns.append(self.prediction_column_name)
    new_data = self.data.reindex(columns, axis= 1)

    for column in self.action_column_names:
      mean = new_data[column].mean()
      std = new_data[column].std()
      new_data.loc[:, column] = (new_data[column] - mean) / std

    return new_data


  def __throw_exception_if_target_test_contains_nan(self, array: np.ndarray) -> None:
    if np.isnan(array).any():
      raise ClassifierException('Error: Actual values are not present.')


