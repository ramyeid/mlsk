#!/usr/bin/python3

import unittest
from classifier.model.classifier_start_request import ClassifierStartRequest


class TestClassifierStartRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_request_id = request.get_request_id()
    actual_prediction_column_name = request.get_prediction_column_name()
    actual_action_column_names = request.get_action_column_names()
    actual_number_of_values = request.get_number_of_values()

    # Then
    expected_request_id = 123
    expected_prediction_column_name = 'Sex'
    expected_action_column_names = ['Length', 'Diameter', 'Height', 'Whole weight']
    expected_number_of_values = 10
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_prediction_column_name, actual_prediction_column_name)
    self.assertEqual(expected_action_column_names, actual_action_column_names)
    self.assertEqual(expected_number_of_values, actual_number_of_values)


  def test_from_json(self) -> None:
    # Given
    json = dict(requestId='123',
                predictionColumnName='Sex',
                actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                numberOfValues='5')

    # When
    actual_request = ClassifierStartRequest.from_json(json)

    # Then
    expected_request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 5)
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=123,
                         predictionColumnName='Sex',
                         actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                         numberOfValues=10)
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 123, 'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 123, 'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request_id_diff = ClassifierStartRequest(1234, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request_pred_column_diff = ClassifierStartRequest(123, 'Sex1', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request_action_columns_diff = ClassifierStartRequest(123, 'Sex', ['Length'], 10)
    request_number_diff = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 1)

    # When
    are_equal1 = request1.__eq__(request_id_diff)
    are_equal2 = request1.__eq__(request_pred_column_diff)
    are_equal3 = request1.__eq__(request_action_columns_diff)
    are_equal4 = request1.__eq__(request_number_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)
    self.assertFalse(are_equal4)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)
    request2 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
