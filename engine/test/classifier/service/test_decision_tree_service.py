#!/usr/bin/python3

import unittest
import pandas as pd
from pandas.testing import assert_frame_equal
from classifier.service.decision_tree_service import DecisionTreeService
from classifier.service.classifier_exception import ClassifierException

class TestDecisionTreeService(unittest.TestCase):


  def test_predict_throw_exception_if_not_all_expected_columns_are_received(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.predict()

    # Then
    self.assertEqual("Error: Column expected (['col0', 'col1', 'col2']) different than received (['col0', 'col1'])", str(context.exception))


  def test_predict_throw_exception_if_columns_received_exceed_columns_expected(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=['0', '1', '2', '3', '4','5', '6', '7'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.predict()

    # Then
    self.assertEqual("Error: Column expected (['col0', 'col2']) different than received (['col0', 'col1', 'col2'])", str(context.exception))


  def test_predict_throw_exception_if_action_column_sizes_are_different(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1], index=['0', '1', '2', '3', '4','5', '6']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=['0', '1', '2', '3', '4','5', '6', '7'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.predict()

    # Then
    self.assertEqual("Error: Action column sizes are not equal; sizes found: [7, 9]", str(context.exception))


  def test_predict_throw_exception_if_prediction_column_size_is_invalid_with_diff_more_than_two(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0], index=['0', '1', '2', '3', '4','5', '6'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.predict()

    # Then
    self.assertEqual("Error: Invalid prediction column size. Prediction: 7, Action: 9, Values to predict: 1", str(context.exception))


  def test_predict_throw_exception_if_prediction_column_size_is_invalid_with_extra_values(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 1, 1], index=['0', '1', '2', '3', '4','5', '6', '7', '8', '9'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.predict()

    # Then
    self.assertEqual("Error: Invalid prediction column size. Prediction: 10, Action: 9, Values to predict: 1", str(context.exception))


  def test_predict_service(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=['0', '1', '2', '3', '4','5', '6', '7'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    actual_data_frame = decision_tree.predict()

    # Then
    data_with_predicted_values = {'col2': [0.0]}
    expected_data_frame = pd.DataFrame.from_dict(data_with_predicted_values)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_predict_service_even_if_value_present(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 1], index=['0', '1', '2', '3', '4','5', '6', '7', '8'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    actual_data_frame = decision_tree.predict()

    # Then
    data_with_predicted_values = {'col2': [0.0]}
    expected_data_frame = pd.DataFrame.from_dict(data_with_predicted_values)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_predict_accuracy_throw_exception_if_not_all_expected_columns_are_received(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    # Then
    self.assertEqual("Error: Column expected (['col0', 'col1', 'col2']) different than received (['col0', 'col1'])", str(context.exception))


  def test_predict_accuracy_throw_exception_if_columns_received_exceed_columns_expected(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=['0', '1', '2', '3', '4','5', '6', '7'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    # Then
    self.assertEqual("Error: Column expected (['col0', 'col2']) different than received (['col0', 'col1', 'col2'])", str(context.exception))


  def test_predict_accuracy_throw_exception_if_action_column_sizes_are_different(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1], index=['0', '1', '2', '3', '4','5', '6']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1], index=['0', '1', '2', '3', '4','5', '6', '7'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    # Then
    self.assertEqual("Error: Action column sizes are not equal; sizes found: [7, 9]", str(context.exception))


  def test_predict_accuracy_throw_exception_if_prediction_column_size_is_invalid_with_diff(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0], index=['0', '1', '2', '3', '4','5', '6'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    # Then
    self.assertEqual("Error: Invalid prediction column size. Prediction: 7, Action: 9, Values to predict: 1", str(context.exception))


  def test_predict_accuracy_throw_exception_if_prediction_column_size_is_invalid_with_extra_values(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 1, 1], index=['0', '1', '2', '3', '4','5', '6', '7', '8', '9'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    with self.assertRaises(ClassifierException) as context:
      decision_tree.compute_predict_accuracy()

    # Then
    self.assertEqual("Error: Invalid prediction column size. Prediction: 10, Action: 9, Values to predict: 1", str(context.exception))


  def test_predict_accuracy(self) -> None:
    # Given
    initial_data = { 'col0': pd.Series([0, 0, 0, 0, 1, 1, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col1': pd.Series([0, 0, 1, 1, 0, 0, 1, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8']),
                     'col2': pd.Series([0, 1, 0, 1, 0, 1, 0, 1, 0], index=['0', '1', '2', '3', '4','5', '6', '7', '8'])}
    decision_tree = DecisionTreeService(pd.DataFrame(initial_data), ['col0', 'col1'], 'col2', 1)

    # When
    actual_accuracy = decision_tree.compute_predict_accuracy()

    # Then
    expected_accuracy = 100.0
    self.assertEqual(expected_accuracy, actual_accuracy)


if __name__ == '__main__':
  unittest.main()
