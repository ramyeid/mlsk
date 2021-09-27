#!/usr/bin/python3

import unittest
from datetime import datetime
from utils import date


class TestDateUtils(unittest.TestCase):


  def test_return_empty_list_if_count_is_zero(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=1)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 0)

    # Then
    self.assertEqual([], actual_next_dates)


  def test_get_next_date_with_seconds_difference(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=1)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 2)

    # Then
    expected_next_date1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=2)
    expected_next_date2 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=3)
    self.assertEqual([expected_next_date1, expected_next_date2], actual_next_dates)


  def test_get_next_date_with_minutes_difference(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=2, hour=10, minute=6, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 3)

    # Then
    expected_next_date1 = datetime(year=1960, month=10, day=2, hour=10, minute=7, second=0)
    expected_next_date2 = datetime(year=1960, month=10, day=2, hour=10, minute=8, second=0)
    expected_next_date3 = datetime(year=1960, month=10, day=2, hour=10, minute=9, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3], actual_next_dates)


  def test_get_next_date_with_hours_difference(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=2, hour=11, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 1)

    # Then
    expected_next_date = datetime(year=1960, month=10, day=2, hour=12, minute=5, second=0)
    self.assertEqual([expected_next_date], actual_next_dates)


  def test_get_next_date_with_days_difference(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=3, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 2)

    # Then
    expected_next_date1 = datetime(year=1960, month=10, day=4, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1960, month=10, day=5, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2], actual_next_dates)


  def test_get_next_date_with_months_difference_with_30_days(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=9, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 3)

    # Then
    expected_next_date1 = datetime(year=1960, month=11, day=2, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1960, month=12, day=2, hour=10, minute=5, second=0)
    expected_next_date3 = datetime(year=1961, month=1, day=2, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3], actual_next_dates)


  def test_get_next_date_with_months_difference_with_31_days(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=10, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=11, day=2, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 4)

    # Then
    expected_next_date1 = datetime(year=1960, month=12, day=2, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1961, month=1, day=2, hour=10, minute=5, second=0)
    expected_next_date3 = datetime(year=1961, month=2, day=2, hour=10, minute=5, second=0)
    expected_next_date4 = datetime(year=1961, month=3, day=2, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3, expected_next_date4] \
            , actual_next_dates)


  def test_get_next_date_with_months_difference_with_february_as_second_date(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=2, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1960, month=3, day=2, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 4)

    # Then
    expected_next_date1 = datetime(year=1960, month=4, day=2, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1960, month=5, day=2, hour=10, minute=5, second=0)
    expected_next_date3 = datetime(year=1960, month=6, day=2, hour=10, minute=5, second=0)
    expected_next_date4 = datetime(year=1960, month=7, day=2, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3, expected_next_date4] \
            , actual_next_dates)


  def test_get_next_date_with_months_difference_and_month_is_december(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=11, day=1, hour=0, minute=0, second=0)
    date_2 = datetime(year=1960, month=12, day=1, hour=0, minute=0, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 3)

    # Then
    expected_next_date1 = datetime(year=1961, month=1, day=1, hour=0, minute=0, second=0)
    expected_next_date2 = datetime(year=1961, month=2, day=1, hour=0, minute=0, second=0)
    expected_next_date3 = datetime(year=1961, month=3, day=1, hour=0, minute=0, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3], actual_next_dates)


  def test_get_next_date_with_year_difference(self) -> None:
    # Given
    date_1 = datetime(year=1960, month=2, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1961, month=2, day=2, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 2)

    # Then
    expected_next_date1 = datetime(year=1962, month=2, day=2, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1963, month=2, day=2, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2], actual_next_dates)


  def test_get_next_date_with_leap_year_difference(self) -> None:
    # Given
    date_1 = datetime(year=1962, month=2, day=2, hour=10, minute=5, second=0)
    date_2 = datetime(year=1963, month=2, day=2, hour=10, minute=5, second=0)

    # When
    actual_next_dates = date.get_next_dates(date_1, date_2, 3)

    # Then
    expected_next_date1 = datetime(year=1964, month=2, day=2, hour=10, minute=5, second=0)
    expected_next_date2 = datetime(year=1965, month=2, day=2, hour=10, minute=5, second=0)
    expected_next_date3 = datetime(year=1966, month=2, day=2, hour=10, minute=5, second=0)
    self.assertEqual([expected_next_date1, expected_next_date2, expected_next_date3], actual_next_dates)


  def test_to_python_date_format(self) -> None:
    # Given
    java_date_format = "yyyy-MM"

    # When
    actual_python_date_format = date.to_python_date_format(java_date_format)

    # Then
    self.assertEqual("%Y-%m", actual_python_date_format)


  def test_to_python_date_format_with_complex_date_format(self) -> None:
    # Given
    java_date_format = "yyyy-MM-dd HH:mm:ss.SSS"

    # When
    actual_python_date_format = date.to_python_date_format(java_date_format)

    # Then
    self.assertEqual("%Y-%m-%d %H:%M:%S.%f", actual_python_date_format)


  def test_to_python_date_format_with_complex_date_format2(self) -> None:
    # Given
    java_date_format = "yy-MM-dd hh:mm:ss.SSS"

    # When
    actual_python_date_format = date.to_python_date_format(java_date_format)

    # Then
    self.assertEqual("%Y-%m-%d %H:%M:%S.%f", actual_python_date_format)


  def test_to_python_date_format_with_complex_date_format_3(self) -> None:
    # Given
    java_date_format = "dd/MM/yy hh:mm:ss.SSS"

    # When
    actual_python_date_format = date.to_python_date_format(java_date_format)

    # Then
    self.assertEqual("%d/%m/%Y %H:%M:%S.%f", actual_python_date_format)


if __name__ == "__main__":
  unittest.main()
