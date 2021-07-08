#!/usr/bin/python3

import unittest
from datetime import datetime
from model.time_series.time_series_analysis_request import TimeSeriesAnalysisRequest
from model.time_series.time_series import TimeSeries
from model.time_series.time_series_row import TimeSeriesRow


class TestTimeSeriesAnalysisRequest(unittest.TestCase):


    def test_from_json(self):
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
                                 "Date", "Passengers", "yyyy-MM")
        expected_time_series_analysis_request = TimeSeriesAnalysisRequest(time_series, 5)
        assert expected_time_series_analysis_request == actual_time_series_analysis_request


if __name__ == "__main__":
    unittest.main()
