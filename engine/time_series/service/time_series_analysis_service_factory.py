#!/usr/bin/python3

import pandas as pd
from time_series.service.time_series_analysis_service import TimeSeriesAnalysisService


class TimeSeriesAnalysisServiceFactory:
  '''
  Builds the time series analysis service
  '''

  def build_service(self, data: pd.DataFrame, date_column_name: str,
                    value_column_name: str, number_of_values: int) -> TimeSeriesAnalysisService:
    return TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)
