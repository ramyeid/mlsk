#!/usr/bin/python3

from time_series.model.time_series_row import TimeSeriesRow
from time_series.model.time_series import TimeSeries


def assert_with_diff(actual: float, expected: float, diff: int) -> None:
  assert abs(expected - actual) < diff


def assert_on_time_series_with_diff(actual: TimeSeries, expected: TimeSeries, diff: int) -> None:
  assert actual.get_date_column_name() == expected.get_date_column_name()
  assert actual.get_value_column_name() == expected.get_value_column_name()
  assert actual.get_date_format() == expected.get_date_format()
  assert len(actual.get_rows()) == len(expected.get_rows())

  for i in range (0, len(actual.get_rows())):
    actual_row: TimeSeriesRow = actual.get_rows()[i]
    expected_row: TimeSeriesRow = expected.get_rows()[i]
    assert actual_row.get_date() == expected_row.get_date()
    assert_with_diff(int(actual_row.get_value()), int(expected_row.get_value()), diff)
