#!/usr/bin/python3

import unittest
from model.classifier.classifier_data_request import ClassifierDataRequest


class TestClassifierDataRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierDataRequest('Sex', [0, 0, 1, 0])

    # When
    actual_column_name = request.get_column_name()
    actual_values = request.get_values()

    # Then
    expected_column_name = 'Sex'
    expected_values = [0, 0, 1, 0]
    self.assertEqual(expected_column_name, actual_column_name)
    self.assertEqual(expected_values, actual_values)


  def test_from_json(self) -> None:
    # Given
    json = dict(columnName='Sex',
                values=['0', '0', '1', '1'])

    # When
    actual_request = ClassifierDataRequest.from_json(json)

    # Then
    expected_request = ClassifierDataRequest('Sex', [0, 0, 1, 1])
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierDataRequest('Sex', [0, 0, 1, 1])

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(columnName='Sex',
                         values=[0, 0, 1, 1])
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierDataRequest('Sex', [0, 1, 1, 1])

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'columnName': 'Sex', 'values': [0, 1, 1, 1]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierDataRequest('Sex', ['0', '1', '1', '1'])

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'columnName': 'Sex', 'values': ['0', '1', '1', '1']}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierDataRequest('Sex1', [0, 1, 1, 1])
    request2 = ClassifierDataRequest('Sex2', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierDataRequest('Sex', [0, 1, 1, 1])
    request2 = ClassifierDataRequest('Sex', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
