#!/usr/bin/python3

import unittest
from datetime import datetime
from time_series.model.time_series_row import TimeSeriesRow


class TestTimeSeriesRow(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    # When
    actual_date = time_series_row.get_date()
    actual_value = time_series_row.get_value()

    # Then
    expected_date = datetime(1949, 1, 1)
    expected_value = 112.0
    self.assertEqual(expected_date, actual_date)
    self.assertEqual(expected_value, actual_value)


  def test_from_json(self) -> None:
    # Given
    json = dict(date='1949-01', value=112.0)

    # When
    actual_time_series_row = TimeSeriesRow.from_json(json, 'yyyy-MM')

    # Then
    expected_time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    self.assertEqual(expected_time_series_row, actual_time_series_row)


  def test_to_json(self) -> None:
    # Given
    time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    # When
    actual_json = time_series_row.to_json('yyyy-MM')

    # Then
    expected_json = dict(date='1949-01', value=112.0)
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    # When
    actual_str = time_series_row.__str__()

    # Then
    expected_str = "{'date': '', 'value': 112.0}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    # When
    actual_str = time_series_row.__repr__()

    # Then
    expected_str = "{'date': '', 'value': 112.0}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 1, 1), 114.0)

    # When
    are_equal = time_series_row1.__eq__(time_series_row2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    # When
    are_equal = time_series_row1.__eq__(time_series_row2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
