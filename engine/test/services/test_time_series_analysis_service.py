#!/usr/bin/python3

import unittest
from datetime import datetime, timedelta
import pandas as pd
from pandas.testing import assert_frame_equal
from services.time_series_analysis_service import TimeSeriesAnalysisService


class TestTimeSeriesAnalysisService(unittest.TestCase):


    def test_predict_service(self):
        # Given
        initial_data = {"Date": [datetime.strptime("1960-08-01 11:00:00", "%Y-%m-%d %H:%M:%S") + timedelta(hours=i)
                                 for i in range(0, 37)],
                        "Value": [112.0, 118.0, 132.0, 129.0, 121.0, 135.0, 148.0,
                                  148.0, 136.0, 119.0, 104.0, 118.0, 115.0, 126.0, 141.0, 135.0, 125.0,
                                  149.0, 170.0, 170.0, 158.0, 158.0, 133.0, 114.0, 140.0, 145.0, 150.0,
                                  178.0, 163.0, 172.0, 178.0, 199.0, 199.0, 184.0, 162.0, 146.0, 166.0]}
        tsa = TimeSeriesAnalysisService(pd.DataFrame.from_dict(initial_data), "Date", "Value", 3)

        # When
        actual_data_frame_with_predicted_values = tsa.predict()

        # Then
        data_with_predicted_values = {"Date": [datetime.strptime("1960-08-01 11:00:00", "%Y-%m-%d %H:%M:%S") + timedelta(hours=i)
                                               for i in range(37, 40)],
                                      "Value": [181.0, 188.0, 193.0]}
        expected_data_frame_with_predicted_values = pd.DataFrame.from_dict(data_with_predicted_values)
        assert_frame_equal(expected_data_frame_with_predicted_values, actual_data_frame_with_predicted_values, check_exact=False, rtol=3)


    def test_forecast_service(self):
        # Given
        initial_data = {"Date": [datetime.strptime("1960-08-01 11:00:00", "%Y-%m-%d %H:%M:%S") + timedelta(days=i)
                                 for i in range(0, 37)],
                        "Value": [112.0, 118.0, 132.0, 129.0, 121.0, 135.0, 148.0,
                                  148.0, 136.0, 119.0, 104.0, 118.0, 115.0, 126.0, 141.0, 135.0, 125.0,
                                  149.0, 170.0, 170.0, 158.0, 158.0, 133.0, 114.0, 140.0, 145.0, 150.0,
                                  178.0, 163.0, 172.0, 178.0, 199.0, 199.0, 184.0, 162.0, 146.0, 166.0]}
        tsa = TimeSeriesAnalysisService(pd.DataFrame.from_dict(initial_data), "Date", "Value", 3)

        # When
        actual_data_frame_with_forecasted_values = tsa.forecast()

        # Then
        data_with_forecasted_values = {"Date": [datetime.strptime("1960-08-01 11:00:00", "%Y-%m-%d %H:%M:%S") + timedelta(days=i)
                                                for i in range(37, 40)],
                                       "Value": [183.0, 188.0, 197.0]}
        expected_data_frame_with_forecasted = pd.DataFrame.from_dict(data_with_forecasted_values)
        assert_frame_equal(expected_data_frame_with_forecasted, actual_data_frame_with_forecasted_values, check_exact=False, rtol=3)


    def test_compute_forecast_accuracy(self):
        # Given
        initial_data = {"Date": [datetime.strptime("1960-08-01 11:00:00", "%Y-%m-%d %H:%M:%S") + timedelta(days=i)
                                 for i in range(0, 37)],
                        "Value": [112.0, 118.0, 132.0, 129.0, 121.0, 135.0, 148.0,
                                  148.0, 136.0, 119.0, 104.0, 118.0, 115.0, 126.0, 141.0, 135.0, 125.0,
                                  149.0, 170.0, 170.0, 158.0, 158.0, 133.0, 114.0, 140.0, 145.0, 150.0,
                                  178.0, 163.0, 172.0, 178.0, 199.0, 199.0, 184.0, 162.0, 146.0, 166.0]}
        tsa = TimeSeriesAnalysisService(pd.DataFrame.from_dict(initial_data), "Date", "Value", 3)

        # When
        actual_accuracy = tsa.compute_forecast_accuracy()

        # Test
        assert 89.74 == actual_accuracy


if __name__ == "__main__":
    unittest.main()
