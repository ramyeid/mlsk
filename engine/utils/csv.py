#!/usr/bin/python3

from datetime import datetime
import pandas as pd


def read(csv_input_file: str, date_column_name: str, date_format: str) -> pd.DataFrame:
    """
    from csv file to dictionary [key(date), value]
    we suppose that the first column is the date.

    Arguments
        - csv_input_file (str)   : location of the csv file
        - date_column_name (str) : name of the column containing date
        - date_format (str)      : format of the date (e.g.: %Y-%m -- according to Python datetime.strftime())

    Returns
        pandas.DataFrame -> data frame read from the csv file
    """

    date_parser = lambda date: datetime.strptime(date, date_format)
    return pd.read_csv(csv_input_file, parse_dates=[date_column_name], date_parser=date_parser)


def write(csv_output_file: str, data: pd.DataFrame, date_column_name: str,  date_format: str) -> str:
    """
    create and write data to_output_file

    Arguments
        - csv_output_file (str)   : location of the output file where the result will be written
        - data (pandas.DataFrame) : initial data as panda frame
        - date_column_name (str)  : name of the column containing date
        - date_format (str)       : format of the date (e.g.: %Y-%m)

    Returns
        str -> csv_output_file; location of the file created.
    """

    data[date_column_name] = pd.to_datetime(data[date_column_name]).dt.strftime(date_format)
    data.to_csv(csv_output_file, index=False)
    return csv_output_file
