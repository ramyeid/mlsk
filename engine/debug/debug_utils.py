#!/usr/bin/python3

from argparse import Namespace
from debug.engine_debug_exception import EngineDebugException


def throw_exception_if_argument_null(argument: str, argument_value: any) -> None:
  if (argument_value is None):
    raise EngineDebugException('Argument {} can not be null'.format(argument))


def get_or_create_output_file(args: Namespace) -> str:
  if args.csv_output is None:
    return args.csv_input.replace('.csv', '_output.csv')

  return args.csv_output
