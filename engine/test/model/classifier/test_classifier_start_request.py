#!/usr/bin/python3

import unittest
from model.classifier.classifier_start_request import ClassifierStartRequest


class TestClassifierStartRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_prediction_column_name = request.get_prediction_column_name()
    actual_action_column_names = request.get_action_column_names()
    actual_number_of_values = request.get_number_of_values()

    # Then
    expected_prediction_column_name = 'Sex'
    expected_action_column_names = ['Length', 'Diameter', 'Height', 'Whole weight']
    expected_number_of_values = 10
    self.assertEqual(expected_prediction_column_name, actual_prediction_column_name)
    self.assertEqual(expected_action_column_names, actual_action_column_names)
    self.assertEqual(expected_number_of_values, actual_number_of_values)


  def test_from_json(self) -> None:
    # Given
    json = dict(predictionColumnName='Sex',
                actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                numberOfValues='5')

    # When
    actual_request = ClassifierStartRequest.from_json(json)

    # Then
    expected_request = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 5)
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(predictionColumnName='Sex',
                         actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                         numberOfValues=10)
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request2 = ClassifierStartRequest('Sex', ['Length1', 'Diameter1', 'Height1', 'Whole weight1'], 10)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request2 = ClassifierStartRequest('Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
