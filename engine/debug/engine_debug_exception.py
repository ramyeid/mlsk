#!/usr/bin/python3

class EngineDebugException(Exception):
  """
  Exception that will be thrown by engine_debug.
  """


def build_action_not_valid_exception(possible_actions: [str], input_action: str) -> EngineDebugException:
  return EngineDebugException("Action not valid: {}. Please choose between {}"
                    .format(input_action, possible_actions))

