#!/usr/bin/python3

import unittest
from classifier.model.classifier_data_request import ClassifierDataRequest


class TestClassifierDataRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierDataRequest(123, 'Sex', [0, 0, 1, 0])

    # When
    actual_request_id = request.get_request_id()
    actual_column_name = request.get_column_name()
    actual_values = request.get_values()

    # Then
    expected_request_id = 123
    expected_column_name = 'Sex'
    expected_values = [0, 0, 1, 0]
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_column_name, actual_column_name)
    self.assertEqual(expected_values, actual_values)


  def test_from_json(self) -> None:
    # Given
    json = dict(requestId=123,
                columnName='Sex',
                values=['0', '0', '1', '1'])

    # When
    actual_request = ClassifierDataRequest.from_json(json)

    # Then
    expected_request = ClassifierDataRequest(123, 'Sex', [0, 0, 1, 1])
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierDataRequest(123, 'Sex', [0, 0, 1, 1])

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=123,
                         columnName='Sex',
                         values=[0, 0, 1, 1])
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierDataRequest(123, 'Sex', [0, 1, 1, 1])

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 123, 'columnName': 'Sex', 'values': [0, 1, 1, 1]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierDataRequest(123, 'Sex', ['0', '1', '1', '1'])

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 123, 'columnName': 'Sex', 'values': ['0', '1', '1', '1']}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierDataRequest(123, 'Sex', [0, 1, 1, 1])
    request_id_diff = ClassifierDataRequest(1234, 'Sex', [0, 1, 1, 1])
    request_column_diff = ClassifierDataRequest(123, 'Sex1', [0, 1, 1, 1])
    request_values_diff = ClassifierDataRequest(123, 'Sex1', [0])

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
    request1 = ClassifierDataRequest(123, 'Sex', [0, 1, 1, 1])
    request2 = ClassifierDataRequest(123, 'Sex', [0, 1, 1, 1])

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
