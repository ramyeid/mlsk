#!/usr/bin/python3


def build_default_response() -> str:
  '''
  Some endpoints do not have a response, but a response is required by Flask.
  The following return value is a contract between the server and the engine.

  Returns
    str -> default api response
  '''

  return '{"Status":"Ok"}'
