#!/usr/bin/python3

import unittest
from datetime import datetime
from process_pool.process import ProcessState
from admin.model.process_detail_response import ProcessDetailResponse


class TestProcessDetailResponse(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    response = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_process_id = response.get_id()
    actual_state = response.get_state()
    actual_flip_flop_count = response.get_flip_flop_count()
    actual_start_datetime = response.get_start_datetime()

    # Then
    expected_process_id = 1
    expected_state = ProcessState.BUSY
    expected_flip_flop_count = 10
    expected_start_datetime = datetime(2000, 10, 10, 10, 10, 10, 10)
    self.assertEqual(expected_process_id, actual_process_id)
    self.assertEqual(expected_state, actual_state)
    self.assertEqual(expected_flip_flop_count, actual_flip_flop_count)
    self.assertEqual(expected_start_datetime, actual_start_datetime)


  def test_to_json(self) -> None:
    # Given
    response = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_json = response.to_json()

    # Then
    expected_json = dict(id=1,
                         state='BUSY',
                         flipFlopCount=10,
                         startDatetime=str(datetime(2000, 10, 10, 10, 10, 10, 10)))
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    response = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_str = response.__str__()

    # Then
    expected_str = "{'id': 1, 'state': 'BUSY', 'flipFlopCount': 10, 'startDatetime': '2000-10-10 10:10:10.000010'}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    response = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    actual_str = response.__repr__()

    # Then
    expected_str = "{'id': 1, 'state': 'BUSY', 'flipFlopCount': 10, 'startDatetime': '2000-10-10 10:10:10.000010'}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    response1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_id_diff = ProcessDetailResponse(2, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_state_diff = ProcessDetailResponse(1, ProcessState.IDLE, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_flip_flop_count_diff = ProcessDetailResponse(1, ProcessState.BUSY, 11, datetime(2000, 10, 10, 10, 10, 10, 10))
    response_start_datetime_diff = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2001, 10, 10, 10, 10, 10, 10))

    # When
    are_equal1 = response1.__eq__(response_id_diff)
    are_equal2 = response1.__eq__(response_state_diff)
    are_equal3 = response1.__eq__(response_flip_flop_count_diff)
    are_equal4 = response1.__eq__(response_start_datetime_diff)

    # Then
    self.assertFalse(are_equal1)
    self.assertFalse(are_equal2)
    self.assertFalse(are_equal3)
    self.assertFalse(are_equal4)


  def test_equal_true(self) -> None:
    # Given
    response1 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))
    response2 = ProcessDetailResponse(1, ProcessState.BUSY, 10, datetime(2000, 10, 10, 10, 10, 10, 10))

    # When
    are_equal = response1.__eq__(response2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
