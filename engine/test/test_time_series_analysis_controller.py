#!/usr/bin/python3

import unittest
import json
from datetime import datetime
import engine
from model.time_series.time_series import TimeSeries
from model.time_series.time_series_row import TimeSeriesRow


test_app = engine.app.test_client()


class TestTimeSeriesAnalysisController(unittest.TestCase):


    def test_forecast(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0),
                                          dict(date="1949-03", value=132.0), dict(date="1949-04", value=129.0),
                                          dict(date="1949-05", value=121.0), dict(date="1949-06", value=135.0),
                                          dict(date="1949-07", value=148.0), dict(date="1949-08", value=148.0),
                                          dict(date="1949-09", value=136.0), dict(date="1949-10", value=119.0),
                                          dict(date="1949-11", value=104.0), dict(date="1949-12", value=118.0),
                                          dict(date="1950-01", value=115.0), dict(date="1950-02", value=126.0),
                                          dict(date="1950-03", value=141.0), dict(date="1950-04", value=135.0),
                                          dict(date="1950-05", value=125.0), dict(date="1950-06", value=149.0),
                                          dict(date="1950-07", value=170.0), dict(date="1950-09", value=158.0),
                                          dict(date="1950-10", value=158.0), dict(date="1950-11", value=133.0),
                                          dict(date="1950-12", value=114.0), dict(date="1951-01", value=140.0),
                                          dict(date="1951-02", value=145.0), dict(date="1951-03", value=150.0),
                                          dict(date="1951-04", value=178.0), dict(date="1951-05", value=163.0),
                                          dict(date="1951-05", value=172.0), dict(date="1951-06", value=178.0),
                                          dict(date="1951-07", value=199.0), dict(date="1951-08", value=199.0),
                                          dict(date="1951-09", value=184.0), dict(date="1951-10", value=162.0),
                                          dict(date="1951-11", value=146.0), dict(date="1951-12", value=166.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM'),
                    numberOfValues=2)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/forecast', data=body_as_string,
                                 content_type='application/json')
        actual_time_series = TimeSeries.from_json(json.loads(response.data))

        # Then
        time_series_row = TimeSeriesRow(datetime(1952, 1, 1), 185.0)
        time_series_row1 = TimeSeriesRow(datetime(1952, 2, 1), 199.0)
        expected_time_series = TimeSeries([time_series_row, time_series_row1], "Date", "Passengers", "yyyy-MM")
        assert expected_time_series == actual_time_series


    def test_forecast_exception(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM-dd'),
                    numberOfValues=2)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/forecast', data=body_as_string,
                                 content_type='application/json')

        # Then
        assert b'"Exception ValueError raised while forecasting: ' \
               b'time data \'1949-01\' does not match format \'%Y-%m-%d\'"\n' == response.data


    def test_compute_forecast_accuracy(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0),
                                          dict(date="1949-03", value=132.0), dict(date="1949-04", value=129.0),
                                          dict(date="1949-05", value=121.0), dict(date="1949-06", value=135.0),
                                          dict(date="1949-07", value=148.0), dict(date="1949-08", value=148.0),
                                          dict(date="1949-09", value=136.0), dict(date="1949-10", value=119.0),
                                          dict(date="1949-11", value=104.0), dict(date="1949-12", value=118.0),
                                          dict(date="1950-01", value=115.0), dict(date="1950-02", value=126.0),
                                          dict(date="1950-03", value=141.0), dict(date="1950-04", value=135.0),
                                          dict(date="1950-05", value=125.0), dict(date="1950-06", value=149.0),
                                          dict(date="1950-07", value=170.0), dict(date="1950-09", value=158.0),
                                          dict(date="1950-10", value=158.0), dict(date="1950-11", value=133.0),
                                          dict(date="1950-12", value=114.0), dict(date="1951-01", value=140.0),
                                          dict(date="1951-02", value=145.0), dict(date="1951-03", value=150.0),
                                          dict(date="1951-04", value=178.0), dict(date="1951-05", value=163.0),
                                          dict(date="1951-05", value=172.0), dict(date="1951-06", value=178.0),
                                          dict(date="1951-07", value=199.0), dict(date="1951-08", value=199.0),
                                          dict(date="1951-09", value=184.0), dict(date="1951-10", value=162.0),
                                          dict(date="1951-11", value=146.0), dict(date="1951-12", value=166.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM'),
                    numberOfValues=1)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/forecast-accuracy', data=body_as_string,
                                 content_type='application/json')
        actual_accuracy = float(response.data)

        # Then
        assert 98.39 == actual_accuracy


    def test_compute_forecast_accuracy_exception(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM-hh'),
                    numberOfValues=1)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/forecast-accuracy', data=body_as_string,
                                 content_type='application/json')
        # Then
        assert b'"Exception ValueError raised while computing forecast accuracy: ' \
               b'time data \'1949-01\' does not match format \'%Y-%m-%H\'"\n' == response.data


    def test_predict(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0),
                                          dict(date="1949-03", value=132.0), dict(date="1949-04", value=129.0),
                                          dict(date="1949-05", value=121.0), dict(date="1949-06", value=135.0),
                                          dict(date="1949-07", value=148.0), dict(date="1949-08", value=148.0),
                                          dict(date="1949-09", value=136.0), dict(date="1949-10", value=119.0),
                                          dict(date="1949-11", value=104.0), dict(date="1949-12", value=118.0),
                                          dict(date="1950-01", value=115.0), dict(date="1950-02", value=126.0),
                                          dict(date="1950-03", value=141.0), dict(date="1950-04", value=135.0),
                                          dict(date="1950-05", value=125.0), dict(date="1950-06", value=149.0),
                                          dict(date="1950-07", value=170.0), dict(date="1950-09", value=158.0),
                                          dict(date="1950-10", value=158.0), dict(date="1950-11", value=133.0),
                                          dict(date="1950-12", value=114.0), dict(date="1951-01", value=140.0),
                                          dict(date="1951-02", value=145.0), dict(date="1951-03", value=150.0),
                                          dict(date="1951-04", value=178.0), dict(date="1951-05", value=163.0),
                                          dict(date="1951-05", value=172.0), dict(date="1951-06", value=178.0),
                                          dict(date="1951-07", value=199.0), dict(date="1951-08", value=199.0),
                                          dict(date="1951-09", value=184.0), dict(date="1951-10", value=162.0),
                                          dict(date="1951-11", value=146.0), dict(date="1951-12", value=166.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM'),
                    numberOfValues=2)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/predict', data=body_as_string,
                                 content_type='application/json')
        actual_time_series = TimeSeries.from_json(json.loads(response.data))

        # Then
        time_series_row = TimeSeriesRow(datetime(1952, 1, 1), 179.0)
        time_series_row1 = TimeSeriesRow(datetime(1952, 2, 1), 189.0)
        expected_time_series = TimeSeries([time_series_row, time_series_row1], "Date", "Passengers", "yyyy-MM")
        assert expected_time_series == actual_time_series


    def test_predict_exception(self):
        # Given
        body = dict(timeSeries=dict(rows=[dict(date="1949-01", value=112.0), dict(date="1949-02", value=118.0)],
                                    dateColumnName='Date',
                                    valueColumnName='Passengers',
                                    dateFormat='yyyy-MM-SS'),
                    numberOfValues=2)
        body_as_string = json.dumps(body)

        # When
        response = test_app.post('/time-series-analysis/predict', data=body_as_string,
                                 content_type='application/json')

        # Then
        assert b'"Exception ValueError raised while predicting: ' \
               b'time data \'1949-01\' does not match format \'%Y-%m-SS\'"\n' == response.data


if __name__ == "__main__":
    unittest.main()
