#!/usr/bin/python3

from datetime import datetime
from dateutil import relativedelta


def get_next_dates(first_date: datetime, second_date: datetime, count: int) -> [datetime]:
  """
  Compute the next [count] dates given the difference of dates
  With first_date < second_date

  Arguments
      - first_date (datetime.datetime)  : first date
      - second_date (datetime.datetime) : second date
      - count (int)                     : number of dates to generate

  Returns
      list[datetime.datetime] -> list containing the next [count] dates
  """

  return [second_date + i * relativedelta.relativedelta(second_date, first_date) for i in range (1, count + 1)]


def to_python_date_format(java_date_format: str) -> str:
  """
  Map the java date format to python date format.
  The Java format is as follows:
    y   = year   (yy or yyyy)
    M   = month  (MM)
    d   = day in month (dd)
    h   = hour (0-12)  (hh)
    H   = hour (0-23)  (HH)
    m   = minute in hour (mm)
    s   = seconds (ss)
    S   = milliseconds (SSS)

  Arguments
      - java_date_format (str) : string representing the java date format

  Returns
        str -> string representing the python date format
  """

  mapping = {'yyyy': '%Y', 'yy': '%Y',
              'MM': '%m',
              'dd': '%d',
              'HH': '%H', 'hh': '%H',
              'mm': '%M',
              'ss': '%S',
              'SSS': '%f'}

  python_date_format = java_date_format
  for key in mapping:
    python_date_format = python_date_format.replace(key, mapping[key])

  return python_date_format
