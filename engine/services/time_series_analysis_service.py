#!/usr/bin/python3

from statsmodels.tsa.arima_model import ARIMA
from datetime import datetime
import pandas as pd
from utils import date


class TimeSeriesAnalysisService:
  """
    Service that will predict the values using time series algorithm
    The data passed to this class are dated.
  """

  def __init__(self, data : pd.DataFrame, date_column_name : str , 
               value_column_name : str, number_of_values : int):
    """
      Create a new TimeSeriesAnalysisService

      Args:
        - data (pandas.DataFrame) : data frame containing the dates and corresponding values
        - date_column_name (str)  : name of the column that contains the date values
        - value_column_name (str) : name of the column that contains the value that will be predicted
        - number_of_values (int)  : predict how many values in the future.
    """

    self.data = data
    self.date_column_name = date_column_name
    self.value_column_name = value_column_name
    self.number_of_values = number_of_values


  def __getNextDate(self, data_frame : pd.DataFrame) -> datetime:
    """
      Computes and returns the next date of a given data_frame by querying the date_column_name.
      This method compares 2 dates of the column date and computes the last one.

      Arguments:
        - data_frame (pandas.DataFrame) : data frame containing the date column

      Returns:
        datetime -> next date of the date column
    """

    date_column = data_frame[self.date_column_name]

    date_1 = date_column.array[-2]
    date_2 = date_column.array[-1]

    return date.get_next_date(date_1, date_2)


  def __copy_data_and_append_values(self, data : pd.DataFrame = None, values : list = []) -> pd.DataFrame:
    """
      Shallow copy data (since old values will not be modified)
      And append the values with their computed dates.

      Arguments:
        - values (list) : values to be appended to the data frame
      
      Returns:
        pandas.DataFrame -> data frame containg the values and dates
    """

    data_with_new_values = None
    if (data is None): 
      data_with_new_values = self.data.copy(deep = False)
    else:
      data_with_new_values = data.copy(deep = False)

    for current_value in values:
      current_index = data_with_new_values.last_valid_index() + 1
      current_date = pd.Timestamp(self.__getNextDate(data_with_new_values))
      data_with_new_values.loc[current_index]  = [current_date, current_value]
    
    return data_with_new_values


  def predict(self) -> pd.DataFrame:
    """
      Predict the next {number_of_values} values to come using ARIMA model
      This method will compute time series algorithm with initial values taken from {data},
      shallow copy {data} and append the predictions with their correspondant dates.

      Returns:
        pandas.DataFrame -> data frame containg the initial data and the new values and dates.
    """

    model = ARIMA(self.data[self.value_column_name], order = (5, 1, 0))
    model_fit = model.fit(disp = 0)
    predictions = model_fit.predict(len(self.data), len(self.data) + self.number_of_values - 1, typ='levels')

    return self.__copy_data_and_append_values(values = predictions)


  def __forecast(self, data : pd.DataFrame) -> pd.DataFrame:
    """
      Forecast the next {number_of_values} using ARIMA on the data passed

      Arguments:
        - data (list) : data for the ARIMA to learn from and forecast
      
      Returns:
        pandas.DataFrame -> data frame containg the forecasted values and dates
    """
    for _ in range(self.number_of_values):
      model = ARIMA(data[self.value_column_name], order = (9, 1, 0))
      model_fit = model.fit(disp=0)
      forecasted_value = model_fit.forecast()
      data = self.__copy_data_and_append_values(data = data, values = forecasted_value[0])

    return data


  def forecast(self) -> pd.DataFrame:
    """
      Forecast the next {number_of_values} values to come using ARIMA model
      This method will compute the forecasted value and add it again to the dataframe to forecast the next one.

      Returns:
        pandas.DataFrame -> data frame containg the initial data and the next values and dates.
    """

    copied_data = self.__copy_data_and_append_values()
    return self.__forecast(copied_data)


  def compute_forecast_accuracy(self) -> float:
    """
      Test the forecast service.
      Forecast the last {number_of_values} values and compare them to the actual values

      Returns:
        float -> computed accuracy of the forecast service
    """

    copied_data = self.__copy_data_and_append_values()
    copied_data.drop(copied_data.tail(self.number_of_values).index, inplace = True)

    data_with_forecasted_values = self.__forecast(copied_data)
    assert len(self.data) == len(data_with_forecasted_values), "initial data and forecasted should be the same length"

    actual_values = self.data[self.value_column_name].values[-self.number_of_values:]
    forecasted_values = data_with_forecasted_values[self.value_column_name].values[-self.number_of_values:]
    sum_of_actual = sum(actual_values)
    sum_of_diff = sum(abs(actual_values[i] - forecasted_values[i]) for i in range(0, self.number_of_values))
    return round(100 * ( 1 - ( (sum_of_diff / self.number_of_values) / (sum_of_actual / self.number_of_values) )), 2)