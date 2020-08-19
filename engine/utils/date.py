#!/usr/bin/python3

from datetime import datetime
from dateutil import relativedelta


def get_next_date(first_date : datetime, second_date : datetime) -> datetime:
  """
    Compute the next date from the difference of dates
    Given that first_date < second_date

    Arguments:
    - first_date (datetime.datetime)  : first date
    - second_date (datetime.datetime) : second date

    Returns:
      datetime.datetime -> date following date_2
  """

  return second_date + relativedelta.relativedelta(second_date, first_date)