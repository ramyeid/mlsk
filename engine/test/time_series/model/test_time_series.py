#!/usr/bin/python3

import unittest
from datetime import datetime, timedelta
import pandas as pd
from pandas.testing import assert_frame_equal
from time_series.model.time_series import TimeSeries
from time_series.model.time_series_row import TimeSeriesRow


class TestTimeSeries(unittest.TestCase):

  DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss'


  def test_getters(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')

    # When
    actual_date_column_name = time_series.get_date_column_name()
    actual_value_column_name = time_series.get_value_column_name()
    actual_date_format = time_series.get_date_format()
    actual_rows = time_series.get_rows()

    # Then
    expected_date_column_name = 'Date'
    expected_value_column_name = 'Passengers'
    expected_date_format = 'yyyy-MM'
    expected_time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    expected_time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    expected_time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    expected_rows = [expected_time_series_row1, expected_time_series_row2, expected_time_series_row3]
    self.assertEqual(expected_date_column_name, actual_date_column_name)
    self.assertEqual(expected_value_column_name, actual_value_column_name)
    self.assertEqual(expected_date_format, actual_date_format)
    self.assertEqual(expected_rows, actual_rows)


  def test_from_json(self) -> None:
    # Given
    json = dict(dateColumnName='Date',
                valueColumnName='Passengers',
                dateFormat='yyyy-MM-dd HH:mm:ss',
                rows=[dict(date='1949-01-01 11:00:01', value=112.0),
                      dict(date='1949-01-01 11:00:02', value=118.0),
                      dict(date='1949-01-01 11:00:03', value=132.0)])

    # When
    actual_time_series = TimeSeries.from_json(json)

    # Then
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1, 11, 0, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 1, 1, 11, 0, 2), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 1, 1, 11, 0, 3), 132.0)
    expected_time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                                      'Date', 'Passengers', self.DATE_FORMAT)
    self.assertEqual(expected_time_series, actual_time_series)


  def test_to_json(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')

    # When
    actual_json = time_series.to_json()

    # Then
    expected_json = dict(dateColumnName='Date',
                          valueColumnName='Passengers',
                          dateFormat='yyyy-MM',
                          rows=[dict(date='1949-01', value=112.0),
                                dict(date='1949-02', value=118.0),
                                dict(date='1949-03', value=132.0)])
    self.assertDictEqual(expected_json, actual_json)


  def test_to_data_frame(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1960, 8, 1, 11, 0, 0), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1960, 8, 1, 12, 0, 0), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1960, 8, 1, 13, 0, 0), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', self.DATE_FORMAT)

    # When
    actual_data_frame = time_series.to_data_frame()

    # Then
    initial_data = {'Date': [datetime.strptime('1960-08-01 11:00:00', '%Y-%m-%d %H:%M:%S') + timedelta(hours=i)
                              for i in range(0, 3)],
                    'Passengers': [112.0, 118.0, 132.0]}
    expected_data_frame = pd.DataFrame.from_dict(initial_data)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_from_data_frame(self) -> None:
    # Given
    initial_data = {'Date': [datetime.strptime('1960-08-01 11:00:00', '%Y-%m-%d %H:%M:%S') + timedelta(hours=i)
                              for i in range(0, 3)],
                    'Passengers': [112.0, 118.0, 132.0]}
    data_frame = pd.DataFrame.from_dict(initial_data)

    # When
    actual_time_series = TimeSeries.from_data_frame(data_frame, 'Date', 'Passengers', self.DATE_FORMAT)

    # Then
    time_series_row1 = TimeSeriesRow(datetime(1960, 8, 1, 11, 0, 0), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1960, 8, 1, 12, 0, 0), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1960, 8, 1, 13, 0, 0), 132.0)
    expected_time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                                      'Date', 'Passengers', self.DATE_FORMAT)
    self.assertEqual(expected_time_series, actual_time_series)


  def test_to_string(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')

    # When
    actual_str = time_series.__str__()

    # Then
    expected_str = "{'dateColumnName': 'Date', 'valueColumnName': 'Passengers', 'dateFormat': 'yyyy-MM', 'rows': [{'date': '1949-01', 'value': 112.0}, {'date': '1949-02', 'value': 118.0}, {'date': '1949-03', 'value': 132.0}]}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')

    # When
    actual_str = time_series.__repr__()

    # Then
    expected_str = "{'dateColumnName': 'Date', 'valueColumnName': 'Passengers', 'dateFormat': 'yyyy-MM', 'rows': [{'date': '1949-01', 'value': 112.0}, {'date': '1949-02', 'value': 118.0}, {'date': '1949-03', 'value': 132.0}]}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series1 = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series2 = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM-dd')

    # When
    are_equal = time_series1.__eq__(time_series2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series1 = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series2 = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')

    # When
    are_equal = time_series1.__eq__(time_series2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
