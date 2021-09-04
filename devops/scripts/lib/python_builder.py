#!/usr/bin/python3

from lib import constants as const
from lib import file_helper
from lib import helper


def compile_python():
  command = 'cd {} && python3 -m compileall -f .'.format(const.ENGINE_DIRECTORY)
  helper.launch_command(command, 'Python compilation failed')


def compile_python_and_run_tests():
  compile_python()
  command = 'cd {} && python3 -m pytest -s'.format(const.ENGINE_DIRECTORY)
  helper.launch_command(command, 'Python test failed')


def copy_engine_project(output: str):
  file_helper.copy_dir(const.ENGINE_DIRECTORY, output)
