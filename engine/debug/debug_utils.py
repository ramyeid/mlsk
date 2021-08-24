#!/usr/bin/python3


def throw_exception_if_argument_null(argument: str, argumentValue: any):
  if (argumentValue is None):
    raise Exception("Argument {} can not be null".format(argument))
