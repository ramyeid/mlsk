#!/usr/bin/python3

import unittest
import pandas as pd
import numpy as np
from pandas.testing import assert_frame_equal
from services.classifier.decision_tree_service import DecisionTreeService
from services.classifier.classifier_exception import ClassifierException

class TestDecisionTreeService(unittest.TestCase):

  def test_predict_service(self):
    # Given
    initial_data = { "col0": pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col1": pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col2": pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=["0", "1", "2", "3", "4","5", "6", "7"])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ["col0", "col1"], "col2", 1)

    # When
    actual_data_frame = decision_tree.predict()

    # Then
    data_with_predicted_values = {"col2": [0.0]}
    expected_data_frame = pd.DataFrame.from_dict(data_with_predicted_values)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_predict_service_even_if_value_present(self):
    # Given
    initial_data = { "col0": pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col1": pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col2": pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 1], index=["0", "1", "2", "3", "4","5", "6", "7", "8"])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ["col0", "col1"], "col2", 1)

    # When
    actual_data_frame = decision_tree.predict()

    # Then
    data_with_predicted_values = {"col2": [0.0]}
    expected_data_frame = pd.DataFrame.from_dict(data_with_predicted_values)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_predict_accuracy(self):
    # Given
    initial_data = { "col0": pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col1": pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col2": pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ["col0", "col1"], "col2", 1)

    # When
    actual_accuracy = decision_tree.compute_predict_accuracy()

    # Then
    expected_accuracy = 100.0
    self.assertEqual(expected_accuracy, actual_accuracy)


  def test_predict_accuracy_throw_exception_if_actual_values_missing(self):
    # Given
    initial_data = { "col0": pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col1": pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=["0", "1", "2", "3", "4","5", "6", "7", "8"]),
                     "col2": pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=["0", "1", "2", "3", "4","5", "6", "7"])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ["col0", "col1"], "col2", 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    self.assertEqual('Error: Actual values are not present.', str(context.exception))


if __name__ == "__main__":
  unittest.main()
