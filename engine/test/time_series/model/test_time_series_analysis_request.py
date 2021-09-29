#!/usr/bin/python3

import unittest
from datetime import datetime
from time_series.model.time_series_analysis_request import TimeSeriesAnalysisRequest
from time_series.model.time_series import TimeSeries
from time_series.model.time_series_row import TimeSeriesRow


class TestTimeSeriesAnalysisRequest(unittest.TestCase):


  def test_getters(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)

    # When
    actual_time_series = time_series_analysis_request.get_time_series()
    actual_number_of_values = time_series_analysis_request.get_number_of_values()

    # Then
    expected_time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    expected_time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    expected_time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    expected_time_series = TimeSeries([expected_time_series_row1, expected_time_series_row2, expected_time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    expected_number_of_values = 5
    self.assertEqual(expected_time_series, actual_time_series)
    self.assertEqual(expected_number_of_values, actual_number_of_values)


  def test_from_json(self) -> None:
    # Given
    json = dict(timeSeries=dict(dateColumnName='Date',
                                valueColumnName='Passengers',
                                dateFormat='yyyy-MM',
                                rows=[dict(date='1949-01', value=112.0),
                                      dict(date='1949-02', value=118.0),
                                      dict(date='1949-03', value=132.0)]),
                numberOfValues=5)

    # When
    actual_time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(json)

    # Then
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    expected_time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)
    self.assertEqual(expected_time_series_analysis_request, actual_time_series_analysis_request)


  def test_to_json(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)

    # When
    actual_json = time_series_analysis_request.to_json()

    # Then
    expected_json = dict(timeSeries=dict(dateColumnName='Date',
                         valueColumnName='Passengers',
                         dateFormat='yyyy-MM',
                         rows=[dict(date='1949-01', value=112.0),
                               dict(date='1949-02', value=118.0),
                               dict(date='1949-03', value=132.0)]),
                         numberOfValues=5)
    self.assertDictEqual(expected_json, actual_json)


  def test_to_string(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)

    # When
    actual_str = time_series_analysis_request.__str__()

    # Then
    expected_str = "{'timeSeries': {'dateColumnName': 'Date', 'valueColumnName': 'Passengers', 'dateFormat': 'yyyy-MM', 'rows': [{'date': '1949-01', 'value': 112.0}, {'date': '1949-02', 'value': 118.0}, {'date': '1949-03', 'value': 132.0}]}, 'numberOfValues': 5}"
    self.assertEqual(expected_str, actual_str)


  def test_repr(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)

    # When
    actual_str = time_series_analysis_request.__repr__()

    # Then
    expected_str = "{'timeSeries': {'dateColumnName': 'Date', 'valueColumnName': 'Passengers', 'dateFormat': 'yyyy-MM', 'rows': [{'date': '1949-01', 'value': 112.0}, {'date': '1949-02', 'value': 118.0}, {'date': '1949-03', 'value': 132.0}]}, 'numberOfValues': 5}"
    self.assertEqual(expected_str, actual_str)


  def test_equal_false(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request1 = TimeSeriesAnalysisRequest(time_series, 5)
    time_series_analysis_request2 = TimeSeriesAnalysisRequest(time_series, 10)

    # When
    are_equal = time_series_analysis_request1.__eq__(time_series_analysis_request2)

    # Then
    self.assertFalse(are_equal)


  def test_equal_true(self) -> None:
    # Given
    time_series_row1 = TimeSeriesRow(datetime(1949, 1, 1), 112.0)
    time_series_row2 = TimeSeriesRow(datetime(1949, 2, 1), 118.0)
    time_series_row3 = TimeSeriesRow(datetime(1949, 3, 1), 132.0)
    time_series = TimeSeries([time_series_row1, time_series_row2, time_series_row3],
                              'Date', 'Passengers', 'yyyy-MM')
    time_series_analysis_request1 = TimeSeriesAnalysisRequest(time_series, 5)
    time_series_analysis_request2 = TimeSeriesAnalysisRequest(time_series, 5)

    # When
    are_equal = time_series_analysis_request1.__eq__(time_series_analysis_request2)

    # Then
    self.assertTrue(are_equal)


if __name__ == '__main__':
  unittest.main()
