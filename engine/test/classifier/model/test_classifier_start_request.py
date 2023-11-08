#!/usr/bin/python3

import unittest
from classifier.model.classifier_start_request import ClassifierStartRequest
from classifier.model.classifier_type import ClassifierType


class TestClassifierStartRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)

    # When
    actual_request_id = request.get_request_id()
    actual_prediction_column_name = request.get_prediction_column_name()
    actual_action_column_names = request.get_action_column_names()
    actual_number_of_values = request.get_number_of_values()
    actual_classifier_type = request.get_classifier_type()

    # Then
    expected_request_id = 123
    expected_prediction_column_name = 'Sex'
    expected_action_column_names = ['Length', 'Diameter', 'Height', 'Whole weight']
    expected_number_of_values = 10
    expected_classifier_type = ClassifierType.DECISION_TREE
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_prediction_column_name, actual_prediction_column_name)
    self.assertEqual(expected_action_column_names, actual_action_column_names)
    self.assertEqual(expected_number_of_values, actual_number_of_values)
    self.assertEqual(expected_classifier_type, actual_classifier_type)


  def test_from_json(self) -> None:
    # Given
    json = dict(requestId='123',
                predictionColumnName='Sex',
                actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                numberOfValues='5',
                classifierType='DECISION_TREE')

    # When
    actual_request = ClassifierStartRequest.from_json(json)

    # Then
    expected_request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 5, ClassifierType.DECISION_TREE)
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=123,
                         predictionColumnName='Sex',
                         actionColumnNames=['Length', 'Diameter', 'Height', 'Whole weight'],
                         numberOfValues=10,
                         classifierType='DECISION_TREE')
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 123, 'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10, 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 123, 'predictionColumnName': 'Sex', 'actionColumnNames': ['Length', 'Diameter', 'Height', 'Whole weight'], 'numberOfValues': 10, 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)
    request_id_diff = ClassifierStartRequest(1234, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)
    request_pred_column_diff = ClassifierStartRequest(123, 'Sex1', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)
    request_action_columns_diff = ClassifierStartRequest(123, 'Sex', ['Length'], 10, ClassifierType.DECISION_TREE)
    request_number_diff = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 1, ClassifierType.DECISION_TREE)
    request_classifier_type_diff = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, None)

    # When
    are_equal1 = request1.__eq__(request_id_diff)
    are_equal2 = request1.__eq__(request_pred_column_diff)
    are_equal3 = request1.__eq__(request_action_columns_diff)
    are_equal4 = request1.__eq__(request_number_diff)
    are_equal5 = request1.__eq__(request_classifier_type_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)
    self.assertFalse(are_equal4)
    self.assertFalse(are_equal5)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)
    request2 = ClassifierStartRequest(123, 'Sex', ['Length', 'Diameter', 'Height', 'Whole weight'], 10, ClassifierType.DECISION_TREE)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
