#!/usr/bin/python3

from model.time_series.time_series import TimeSeries


class TimeSeriesAnalysisRequest:
  """
    Represents the request that will be sent to the time_series_analysis_service

    Attributes
      - time_series (TimeSeries) : time series object that is equivalent to a csv
      - number_of_values (int)   : count of values to forecast
  """

  def __init__(self, time_series : TimeSeries, number_of_values : int):
    self.time_series = time_series
    self.number_of_values = number_of_values


  def get_time_series(self) -> TimeSeries:
    return self.time_series


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def __eq__(self, other):
        return isinstance(other, TimeSeriesAnalysisRequest) and self.time_series == other.time_series and self.number_of_values == other.number_of_values


  @classmethod
  def from_json(cls, data : dict):
    timeSeries = TimeSeries.from_json(data["timeSeries"])
    return cls(timeSeries, int(data["numberOfValues"]))