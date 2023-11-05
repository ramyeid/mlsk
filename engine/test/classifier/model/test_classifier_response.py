#!/usr/bin/python3

import unittest
import pandas as pd
from classifier.model.classifier_response import ClassifierResponse


class TestClassifierResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierResponse(1, 'Sex', [0, 0, 1, 0])

    # When
    actual_request_id = request.get_request_id()
    actual_column_name = request.get_column_name()
    actual_values = request.get_values()

    # Then
    expected_request_id = 1
    expected_column_name = 'Sex'
    expected_values = [0, 0, 1, 0]
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_column_name, actual_column_name)
    self.assertEqual(expected_values, actual_values)

  def test_to_json(self) -> None:
    # Given
    request = ClassifierResponse(1, 'Sex', [0, 0, 1, 1])

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=1,
                         columnName='Sex',
                         values=[0, 0, 1, 1])
    self.assertDictEqual(expected_json, actual_json)


  def test_from_data_frame(self) -> None:
    # Given
    data_frame = pd.DataFrame.from_dict({'col2': [0.0, 1.0, 0.0, 0.0]})

    # When
    actual_classifier_response = ClassifierResponse.from_data_frame(data_frame, 1, 'col2')

    # Then
    expected_classifier_response = ClassifierResponse(1, 'col2', [0, 1, 0, 0])
    self.assertEqual(expected_classifier_response, actual_classifier_response)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierResponse(1, 'Sex', [0, 1, 1, 1])

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 1, 'columnName': 'Sex', 'values': [0, 1, 1, 1]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierResponse(1, 'Sex', ['0', '1', '1', '1'])

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 1, 'columnName': 'Sex', 'values': ['0', '1', '1', '1']}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierResponse(1, 'Sex1', [0, 1, 1, 1])
    request_id_diff = ClassifierResponse(2, 'Sex1', [0, 1, 1, 1])
    request_column_diff = ClassifierResponse(1, 'Sex2', [0, 1, 1, 1])
    request_values_diff = ClassifierResponse(1, 'Sex1', [0])

    # When
    are_equal1 = request1.__eq__(request_id_diff)
    are_equal2 = request1.__eq__(request_column_diff)
    are_equal3 = request1.__eq__(request_values_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierResponse(1, 'Sex', [0, 1, 1, 1])
    request2 = ClassifierResponse(1, 'Sex', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
