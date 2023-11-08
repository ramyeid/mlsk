#!/usr/bin/python3

import unittest
from classifier.model.classifier_request import ClassifierRequest
from classifier.model.classifier_type import ClassifierType


class TestClassifierRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    request = ClassifierRequest(123, ClassifierType.DECISION_TREE)

    # When
    actual_request_id = request.get_request_id()
    actual_classifier_type = request.get_classifier_type()

    # Then
    expected_request_id = 123
    expected_classifier_type = ClassifierType.DECISION_TREE
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_classifier_type, actual_classifier_type)


  def test_from_json(self) -> None:
    # Given
    json = dict(requestId='123', classifierType='DECISION_TREE')

    # When
    actual_request = ClassifierRequest.from_json(json)

    # Then
    expected_request = ClassifierRequest(123, ClassifierType.DECISION_TREE)
    self.assertEqual(expected_request, actual_request)


  def test_to_json(self) -> None:
    # Given
    request = ClassifierRequest(123, ClassifierType.DECISION_TREE)

    # When
    actual_json = request.to_json()

    # Then
    expected_json = dict(requestId=123, classifierType='DECISION_TREE')
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    request = ClassifierRequest(123, ClassifierType.DECISION_TREE)

    # When
    actual_str = request.__str__()

    # Then
    expected_str = "{'requestId': 123, 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    request = ClassifierRequest(123, ClassifierType.DECISION_TREE)

    # When
    actual_str = request.__repr__()

    # Then
    expected_str = "{'requestId': 123, 'classifierType': 'DECISION_TREE'}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    request1 = ClassifierRequest(123, ClassifierType.DECISION_TREE)
    request_id_diff = ClassifierRequest(1234, ClassifierType.DECISION_TREE)
    request_classifier_type_diff = ClassifierRequest(123, None)

    # When
    are_equal1 = request1.__eq__(request_id_diff)
    are_equal2 = request1.__eq__(request_classifier_type_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)


  def test_equal_true(self) -> None:
    # Given
    request1 = ClassifierRequest(123, ClassifierType.DECISION_TREE)
    request2 = ClassifierRequest(123, ClassifierType.DECISION_TREE)

    # When
    are_equal = request1.__eq__(request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
