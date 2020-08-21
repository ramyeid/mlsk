#!/usr/bin/python3

import unittest
from model.time_series.time_series_row import TimeSeriesRow
from datetime import datetime


class TestTimeSeriesRow(unittest.TestCase):


  def test_to_json(self):
    #Given
    time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)

    #When
    actual_json = time_series_row.to_json("%Y-%m")

    #Then
    expected_json = dict(date = '1949-01', value = 112.0)
    assert expected_json == actual_json


  def test_from_json(self):
    #Given
    json = dict(date = '1949-01', value = 112.0)

    #When
    actual_time_series_row = TimeSeriesRow.from_json(json, "%Y-%m")

    #Then
    expected_time_series_row = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    assert expected_time_series_row == actual_time_series_row


if __name__ == "__main__":
  unittest.main()