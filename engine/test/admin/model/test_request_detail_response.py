#!/usr/bin/python3

import unittest
from datetime import datetime
from engine_state import RequestType
from admin.model.request_detail_response import RequestDetailResponse


class TestRequestDetailResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    response = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_request_id = response.get_id()
    actual_request_type = response.get_type()
    actual_creation_datetime = response.get_creation_datetime()

    # Then
    expected_request_id = 1
    expected_request_type = RequestType.CLASSIFIER
    expected_creation_datetime = datetime(2000, 10, 10, 10, 10, 10, 10)
    self.assertEqual(expected_request_id, actual_request_id)
    self.assertEqual(expected_request_type, actual_request_type)
    self.assertEqual(expected_creation_datetime, actual_creation_datetime)


  def test_to_json(self) -> None:
    # Given
    response = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_json = response.to_json()

    # Then
    expected_json = dict(id=1,
                         type='CLASSIFIER',
                         creationDatetime='2000-10-10 10:10:10.000010')
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    response = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_str = response.__str__()

    # Then
    expected_str = "{'id': 1, 'type': 'CLASSIFIER', 'creationDatetime': '2000-10-10 10:10:10.000010'}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    response = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_str = response.__repr__()

    # Then
    expected_str = "{'id': 1, 'type': 'CLASSIFIER', 'creationDatetime': '2000-10-10 10:10:10.000010'}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    response1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_id_diff = RequestDetailResponse(2, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_type_diff = RequestDetailResponse(1, RequestType.TIME_SERIES_ANALYSIS, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_creation_datetime_diff = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2001, 10, 10, 10, 10, 10, 10))

    # When
    are_equal1 = response1.__eq__(response_id_diff)
    are_equal2 = response1.__eq__(response_type_diff)
    are_equal3 = response1.__eq__(response_creation_datetime_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)


  def test_equal_true(self) -> None:
    # Given
    response1 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))
    response2 = RequestDetailResponse(1, RequestType.CLASSIFIER, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    are_equal = response1.__eq__(response2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
