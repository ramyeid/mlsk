#!/usr/bin/python3

from __future__ import annotations
from time_series.model.time_series import TimeSeries


class TimeSeriesAnalysisRequest:
  '''
  Represents the request that will be sent to the time_series_analysis_service

  Attributes
    request_id (int)  - unique id assigned to a request
    time_series (TimeSeries) - time series object that is equivalent to a csv
    number_of_values (int)   - count of values to forecast
  '''


  def __init__(self, request_id: int, time_series: TimeSeries, number_of_values: int):
    self.request_id = request_id
    self.time_series = time_series
    self.number_of_values = number_of_values


  def get_request_id(self) -> int:
    return self.request_id


  def get_time_series(self) -> TimeSeries:
    return self.time_series


  def get_number_of_values(self) -> int:
    return self.number_of_values


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, TimeSeriesAnalysisRequest) and\
      self.request_id == other.request_id and\
      self.time_series == other.time_series and\
      self.number_of_values == other.number_of_values


  def to_json(self) -> dict:
    return dict(requestId = self.request_id, timeSeries=self.time_series.to_json(), numberOfValues=self.number_of_values)


  @classmethod
  def from_json(cls, data: dict) -> TimeSeriesAnalysisRequest:
    request_id = int(data['requestId'])
    time_series = TimeSeries.from_json(data['timeSeries'])
    return cls(request_id, time_series, int(data['numberOfValues']))
