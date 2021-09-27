#!/usr/bin/python3

import unittest
import pandas as pd
from model.classifier.classifier_data_response import ClassifierDataResponse


class TestClassifierDataResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierDataResponse('Sex', [0, 0, 1, 0])

    # When
    actual_column_name = request.get_column_name()
    actual_values = request.get_values()

    # Then
    expected_column_name = 'Sex'
    expected_values = [0, 0, 1, 0]
    self.assertEqual(expected_column_name, actual_column_name)
    self.assertEqual(expected_values, actual_values)

  def test_to_json(self) -> None:
    # Given
    request = ClassifierDataResponse('Sex', [0, 0, 1, 1])

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(columnName='Sex',
                         values=[0, 0, 1, 1])
    self.assertDictEqual(expected_json, actual_json)


  def test_from_data_frame(self) -> None:
    # Given
    data_frame = pd.DataFrame.from_dict({'col2': [0.0, 1.0, 0.0, 0.0]})

    # When
    actual_classifier_data_request = ClassifierDataResponse.from_data_frame(data_frame, 'col2')

    # Then
    expected_classifier_data_request = ClassifierDataResponse('col2', [0, 1, 0, 0])
    self.assertEqual(expected_classifier_data_request, actual_classifier_data_request)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierDataResponse('Sex', [0, 1, 1, 1])

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'columnName': 'Sex', 'values': [0, 1, 1, 1]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierDataResponse('Sex', ['0', '1', '1', '1'])

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'columnName': 'Sex', 'values': ['0', '1', '1', '1']}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierDataResponse('Sex1', [0, 1, 1, 1])
    request2 = ClassifierDataResponse('Sex2', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierDataResponse('Sex', [0, 1, 1, 1])
    request2 = ClassifierDataResponse('Sex', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
