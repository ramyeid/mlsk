#!/usr/bin/python3

import unittest
import pandas as pd
from classifier.model.classifier_response import ClassifierResponse
from classifier.model.classifier_type import ClassifierType


class TestClassifierResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    response = ClassifierResponse(1, 'Sex', [0, 0, 1, 0], ClassifierType.DECISION_TREE)

    # When
    actual_request_id = response.get_request_id()
    actual_column_name = response.get_column_name()
    actual_values = response.get_values()
    actual_classifier_type = response.get_classifier_type()

    # Then
    expected_request_id = 1
    expected_column_name = 'Sex'
    expected_values = [0, 0, 1, 0]
    expected_classifier_type = ClassifierType.DECISION_TREE
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_column_name, actual_column_name)
    self.assertEqual(expected_values, actual_values)
    self.assertEqual(expected_classifier_type, actual_classifier_type)


  def test_to_json(self) -> None:
    # Given
    response = ClassifierResponse(1, 'Sex', [0, 0, 1, 1], ClassifierType.DECISION_TREE)

    # When
    actual_json = response.to_json()

    # Then
    expected_json = dict(requestId=1,
                         columnName='Sex',
                         values=[0, 0, 1, 1],
                         classifierType='DECISION_TREE')
    self.assertDictEqual(expected_json, actual_json)


  def test_from_data_frame(self) -> None:
    # Given
    data_frame = pd.DataFrame.from_dict({'col2': [0.0, 1.0, 0.0, 0.0]})

    # When
    actual_classifier_response = ClassifierResponse.from_data_frame(data_frame, 1, 'col2', ClassifierType.DECISION_TREE)

    # Then
    expected_classifier_response = ClassifierResponse(1, 'col2', [0, 1, 0, 0], ClassifierType.DECISION_TREE)
    self.assertEqual(expected_classifier_response, actual_classifier_response)


  def test_to_string(self) -> None:
    # Given
    response = ClassifierResponse(1, 'Sex', [0, 1, 1, 1], ClassifierType.DECISION_TREE)

    # When
    actual_str = response.__str__()

    # Then
    expected_str = "{'requestId': 1, 'columnName': 'Sex', 'values': [0, 1, 1, 1], 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    response = ClassifierResponse(1, 'Sex', ['0', '1', '1', '1'], ClassifierType.DECISION_TREE)

    # When
    actual_str = response.__repr__()

    # Then
    expected_str = "{'requestId': 1, 'columnName': 'Sex', 'values': ['0', '1', '1', '1'], 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    response1 = ClassifierResponse(1, 'Sex1', [0, 1, 1, 1], ClassifierType.DECISION_TREE)
    response_id_diff = ClassifierResponse(2, 'Sex1', [0, 1, 1, 1], ClassifierType.DECISION_TREE)
    response_column_diff = ClassifierResponse(1, 'Sex2', [0, 1, 1, 1], ClassifierType.DECISION_TREE)
    response_values_diff = ClassifierResponse(1, 'Sex1', [0], ClassifierType.DECISION_TREE)
    response_classifier_type_diff = ClassifierResponse(1, 'Sex1', [0, 1, 1, 1], None)

    # When
    are_equal1 = response1.__eq__(response_id_diff)
    are_equal2 = response1.__eq__(response_column_diff)
    are_equal3 = response1.__eq__(response_values_diff)
    are_equal4 = response1.__eq__(response_classifier_type_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)
    self.assertFalse(are_equal4)


  def test_equal_true(self) -> None:
    # Given
    response1 = ClassifierResponse(1, 'Sex', [0, 1, 1, 1], ClassifierType.DECISION_TREE)
    response2 = ClassifierResponse(1, 'Sex', [0, 1, 1, 1], ClassifierType.DECISION_TREE)

    # When
    are_equal = response1.__eq__(response2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
