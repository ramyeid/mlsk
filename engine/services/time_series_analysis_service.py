#!/usr/bin/python3

from datetime import datetime
from statsmodels.tsa.arima_model import ARIMA
import pandas as pd
import numpy as np
from utils import date


class TimeSeriesAnalysisService:
    """
    Service that will predict the values using time series algorithm
    The data passed to this class are dated.

    Attributes
        - data (pandas.DataFrame) : data frame containing the dates and corresponding values
        - date_column_name (str)  : name of the column that contains the date values
        - value_column_name (str) : name of the column that contains the value that will be predicted
        - number_of_values (int)  : predict how many values in the future.
    """

    def __init__(self, data: pd.DataFrame, date_column_name: str,
                 value_column_name: str, number_of_values: int):
        self.data = data
        self.date_column_name = date_column_name
        self.value_column_name = value_column_name
        self.number_of_values = number_of_values


    def __get_next_dates(self) -> [datetime]:
        """
        Computes and returns the next {count} date of a given data_frame by querying the date_column_name.
        This method compares 2 dates of the column date and computes the last one.

        Arguments
            - data_frame (pandas.DataFrame) : data frame containing the date column
            - count (int): number of dates to generate

        Returns
            list[datetime] -> next {count} date of the date column
        """

        date_column = self.data[self.date_column_name]

        date_1 = date_column.array[-2]
        date_2 = date_column.array[-1]

        return date.get_next_dates(date_1, date_2, self.number_of_values)


    def __create_data_frame_with_values(self, values: pd.array) -> pd.DataFrame:
        """
        Creates a data frame with computed values and dates

        Arguments
            - values (pd.array): computed values

        Returns
            pandas.DataFrame -> data frame containing the computed values and corresponding dates
        """

        dates = self. __get_next_dates()
        data_frame_as_dict = {self.date_column_name: [pd.Timestamp(date) for date in dates],
                              self.value_column_name: values}

        return pd.DataFrame.from_dict(data_frame_as_dict)


    def predict(self) -> pd.DataFrame:
        """
        Predict the next {number_of_values} values to come using ARIMA model
        This method will compute time series algorithm with initial values taken from [data]

        Returns
            pandas.DataFrame -> data frame containing the new values and dates.
        """

        model = ARIMA(self.data[self.value_column_name], order=(5, 1, 0))
        model_fit = model.fit(disp=0)
        predictions = model_fit.predict(len(self.data), len(self.data) + self.number_of_values - 1, typ='levels')

        return self.__create_data_frame_with_values(predictions.array)


    def __forecast(self, data: pd.DataFrame) -> pd.DataFrame:
        """
        Forecast the next {number_of_values} using ARIMA on the data passed

        Arguments
            - data (pd.Dataframe) : dataframe learn from and forecast

        Returns
            pandas.DataFrame -> data frame containing the forecasted values and dates
        """

        value_column = np.asarray(data[self.value_column_name])
        for _ in range(self.number_of_values):
            model = ARIMA(value_column, order=(9, 1, 0))
            model_fit = model.fit(disp=0)
            forecasted_value = model_fit.forecast()
            value_column = np.append(value_column, forecasted_value[0])

        return self.__create_data_frame_with_values(value_column[-self.number_of_values:])


    def forecast(self) -> pd.DataFrame:
        """
        Forecast the next {number_of_values} values to come using ARIMA model
        This method will compute the forecasted value and create a dataframe with the computed results and dates

        Returns
            pandas.DataFrame -> data frame containing computed values and dates.
        """

        return self.__forecast(self.data)


    def compute_forecast_accuracy(self) -> float:
        """
        Test the forecast service.
        Forecast the last {number_of_values} values and compare them to the actual values

        Returns
            float -> computed accuracy of the forecast service
        """

        copied_data = self.data.copy(deep=False)
        copied_data.drop(copied_data.tail(self.number_of_values).index, inplace=True)

        forecasted_data_frame = self.__forecast(copied_data)
        assert len(forecasted_data_frame) == self.number_of_values, \
               "forecasted data frame should be equal to number of values"

        actual_values = self.data[self.value_column_name].values[-self.number_of_values:]
        forecasted_values = forecasted_data_frame[self.value_column_name].values
        sum_of_actual = sum(actual_values)
        sum_of_diff = sum(abs(actual_values[i] - forecasted_values[i]) for i in range(0, self.number_of_values))
        return round(100 * (1 - ((sum_of_diff / self.number_of_values) / (sum_of_actual / self.number_of_values))), 2)
