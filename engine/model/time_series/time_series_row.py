#!/usr/bin/python3

from datetime import datetime
from utils.date import to_python_date_format


class TimeSeriesRow:
  """
  Represents each row in the TimeSeries that will be analyzed by time_series_analysis_service

  Attributes
      - date (datetime) : date in the row
      - value (float)   : corresponding value
  """


  def __init__(self, date: datetime, value: float):
    self.date = date
    self.value = value


  def get_date(self) -> datetime:
    return self.date


  def get_value(self) -> float:
    return self.value


  def __str__(self) -> str:
    return str(self.to_json(''))


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other):
    return isinstance(other, TimeSeriesRow) and self.date == other.date and int(self.value) == int(other.value)


  def to_json(self, date_format: str) -> dict:
    return dict(date=self.date.strftime(to_python_date_format(date_format)),
                value=float(self.value))


  @classmethod
  def from_json(cls, data: dict, date_format: str):
    date = datetime.strptime(data["date"], to_python_date_format(date_format))
    return cls(date, float(data["value"]))
