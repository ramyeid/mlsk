
#!/usr/bin/python3

from model.time_series.time_series_row import TimeSeriesRow
import pandas as pd


class TimeSeries:
  """
    Represents the object that will be analyzed by time_series_analysis_service

    Attributes
      - rows (list[TimeSeriesRow]) : rows corresponding to the csv file
      - date_column_name (str)  : name of the column that contains the date values
      - value_column_name (str) : name of the column that contains the value that will be predicted
      - dateFormat (str)          : date format of the date values e.g.: "%Y-%m" (according to Python strftime())
  """

  def __init__(self, rows : [TimeSeriesRow], date_column_name : str, value_column_name : str, date_format : str):
    self.rows = rows
    self.date_column_name = date_column_name
    self.value_column_name = value_column_name
    self.date_format = date_format


  def get_rows(self) -> [TimeSeriesRow]:
    return self.rows


  def get_date_column_name(self) -> str:
    return self.date_column_name


  def get_value_column_name(self) -> str:
    return self.value_column_name


  def get_date_format(self) -> float:
    return self.date_format


  def __eq__(self, other):
    return isinstance(other, TimeSeries) and self.date_column_name == other.date_column_name and self.value_column_name == other.value_column_name and self.date_format == other.date_format and self.rows == other.rows


  def to_json(self) -> dict:
    return dict(dateColumnName = self.date_column_name,
                valueColumnName = self.value_column_name,
                dateFormat = self.date_format,
                rows = list(map(lambda row : row.to_json(self.date_format), self.rows)))


  def to_data_frame(self) -> pd.DataFrame:
    data = {self.get_date_column_name() : [ row.get_date()  for row in self.get_rows()],
            self.get_value_column_name(): [ row.get_value() for row in self.get_rows()]}
    return pd.DataFrame.from_dict(data)


  @classmethod
  def from_json(cls, json : dict):
    date_column_name = str(json["dateColumnName"])
    value_column_name = str(json["valueColumnName"])
    date_format = str(json["dateFormat"])
    rows = list(map(lambda json : TimeSeriesRow.from_json(json, date_format), json["rows"]))
    return cls(rows, date_column_name, value_column_name, date_format)


  @classmethod
  def from_data_frame(cls, data_frame : pd.DataFrame, date_column_name : str, value_column_name, date_format : str):
    dates = data_frame[date_column_name].values
    values = data_frame[value_column_name].values

    rows = [TimeSeriesRow(pd.to_datetime(dates[i]), values[i]) for i in range(0, len(dates))]
    return TimeSeries(rows, date_column_name, value_column_name, date_format)