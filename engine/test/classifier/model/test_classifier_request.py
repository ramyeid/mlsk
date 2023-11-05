#!/usr/bin/python3

import unittest
from classifier.model.classifier_request import ClassifierRequest


class TestClassifierRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierRequest(123)

    # When
    actual_request_id = request.get_request_id()

    # Then
    expected_request_id = 123
    self.assertEqual(expected_request_id, actual_request_id)


  def test_from_json(self) -> None:
    # Given
    json = dict(requestId='123')

    # When
    actual_request = ClassifierRequest.from_json(json)

    # Then
    expected_request = ClassifierRequest(123)
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierRequest(123)

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=123)
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierRequest(123)

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 123}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierRequest(123)

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 123}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierRequest(123)
    request_id_diff = ClassifierRequest(1234)

    # When
    are_equal1 = request1.__eq__(request_id_diff)

    # Then
    self.assertFalse(are_equal1)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierRequest(123)
    request2 = ClassifierRequest(123)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
