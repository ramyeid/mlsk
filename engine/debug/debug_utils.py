#!/usr/bin/python3

from engine.debug.engine_debug_exception import EngineDebugException


def throw_exception_if_argument_null(argument: str, argument_value: any):
  if (argument_value is None):
    raise EngineDebugException("Argument {} can not be null".format(argument))
